import asyncio

from bleak import BleakClient

ADDRESS = "28:CD:C1:03:39:4F"
WRITE_CHARACTERISTIC_UUID = "6E400002-B5A3-F393-E0A9-E50E24DCCA9E"
READ_CHARACTERISTIC_UUID = "6E400003-B5A3-F393-E0A9-E50E24DCCA9E"

received_message = None


def notification_handler(sender, data):
    global received_message
    received_message = data.decode('utf-8')
    print(received_message)


async def send_and_receive(client, message):
    global received_message
    received_message = None

    await client.write_gatt_char(WRITE_CHARACTERISTIC_UUID, bytearray(message, 'utf-8'))

    # Wait until a message is received
    while received_message is None:
        await asyncio.sleep(0.1)


async def main():
    client = BleakClient(ADDRESS)
    print("Connecting...")
    await client.connect()

    if not client.is_connected:
        raise ConnectionError(f"Failed to connect to {ADDRESS}")

    print("Connected to: " + client.address)
    print("Process has been started.")
    commands = ["test",
                "single_move",
                "single_move 170 5000",
                "full_move 180 1000",
                "full_move 90 500",
                "full)move",
                "full_move 12 500"
                ]
    for cmd in commands:
        await client.start_notify(READ_CHARACTERISTIC_UUID, notification_handler)
        await send_and_receive(client, cmd)
        await client.stop_notify(READ_CHARACTERISTIC_UUID)

    await client.disconnect()


if __name__ == "__main__":
    asyncio.run(main())
