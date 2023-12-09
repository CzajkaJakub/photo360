import asyncio
from bleak import BleakClient
import camera
import sys

ADDRESS = "28:CD:C1:03:39:4F"
WRITE_CHARACTERISTIC_UUID = "6E400002-B5A3-F393-E0A9-E50E24DCCA9E"
READ_CHARACTERISTIC_UUID = "6E400003-B5A3-F393-E0A9-E50E24DCCA9E"

received_message = None


def notification_handler(sender, data):
    global received_message
    received_message = data.decode('utf-8')
    print(received_message)


def make_photo():
    camera.capture_photo()


async def send_and_receive(client, message):
    global received_message
    received_message = None

    await client.write_gatt_char(WRITE_CHARACTERISTIC_UUID, bytearray(message, 'utf-8'))

    while True:
        while received_message is None:
            await asyncio.sleep(0.1)

        if received_message == "PHOTO":
            await asyncio.sleep(0.2)
            make_photo()
            await client.write_gatt_char(WRITE_CHARACTERISTIC_UUID, bytearray("next", 'utf-8'))
            received_message = None
        else:
            break


async def main(commands):
    client = BleakClient(ADDRESS)
    print("Connecting...")
    await client.connect()

    if not client.is_connected:
        raise ConnectionError(f"Failed to connect to {ADDRESS}")

    print("Connected to: " + client.address)
    print("Process has been started.")

    for cmd in commands:
        await client.start_notify(READ_CHARACTERISTIC_UUID, notification_handler)
        await send_and_receive(client, cmd)
        await client.stop_notify(READ_CHARACTERISTIC_UUID)
    await client.disconnect()


if __name__ == "__main__":
    if len(sys.argv) < 3:
        print("Usage: myscript.py <move type 1> <degree 1> <move type 2> <degree 2> ...")
        sys.exit(1)

    arguments = []
    for i in range(1, len(sys.argv), 2):
        com_str = f"{sys.argv[i]} {sys.argv[i + 1]}"
        arguments.append(com_str)

    asyncio.run(main(arguments))
