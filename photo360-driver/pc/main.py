import asyncio
from bleak import BleakClient
import camera
import os
import sys
from background_remover import remove_background

ADDRESS = "28:CD:C1:03:39:4F"
WRITE_CHARACTERISTIC_UUID = "6E400002-B5A3-F393-E0A9-E50E24DCCA9E"
READ_CHARACTERISTIC_UUID = "6E400003-B5A3-F393-E0A9-E50E24DCCA9E"

received_message = None


def notification_handler(sender, data):
    global received_message
    received_message = data.decode('utf-8')


def make_photo(folder_path, cam):
    camera.capture_photo(folder_path, cam)


async def send_and_receive(client, message, folder_path, cam):
    global received_message
    received_message = None

    await client.write_gatt_char(WRITE_CHARACTERISTIC_UUID, bytearray(message, 'utf-8'))

    while True:
        while received_message is None:
            await asyncio.sleep(0.1)

        if received_message == "PHOTO":
            await asyncio.sleep(0.2)
            make_photo(folder_path, cam)
            await client.write_gatt_char(WRITE_CHARACTERISTIC_UUID, bytearray("next", 'utf-8'))
            received_message = None
        else:
            break


async def main(commands, folder_path):
    client = BleakClient(ADDRESS)

    try:
        await client.connect()
    except:
        sys.stderr.write("102\n")
        sys.exit(102)

    usb_webcam = camera.find_usb_camera()
    if usb_webcam is None:
        sys.stderr.write("103\n")
        sys.exit(103)

    for cmd in commands:
        await client.start_notify(READ_CHARACTERISTIC_UUID, notification_handler)

        # Add target catalog of a photo to save path
        comm_mode = cmd.split(' ')[0]
        if comm_mode == "single_move":
            folder_path_final = folder_path + "/single"
        elif comm_mode == "full_move":
            folder_path_final = folder_path + "/full"
        else:
            sys.stderr.write("105\n")
            sys.exit(105)

        # Create target catalog if not exists
        if not os.path.exists(folder_path_final):
            os.makedirs(folder_path_final)

        await send_and_receive(client, cmd, folder_path_final, usb_webcam)
        await client.stop_notify(READ_CHARACTERISTIC_UUID)
    await client.disconnect()
    remove_background(folder_path)


# Usage: myscript.py <folder_path> <move type 1> <degree 1> <move type 2> <degree 2> ...
if __name__ == "__main__":
    if len(sys.argv) < 4:
        sys.stderr.write("101\n")
        sys.exit(101)

    folder_path = sys.argv[1]
    if os.path.exists(folder_path):
        sys.stderr.write("104\n")
        sys.exit(104)

    arguments = []
    for i in range(2, len(sys.argv), 2):
        com_str = f"{sys.argv[i]} {sys.argv[i + 1]}"
        arguments.append(com_str)

    asyncio.run(main(arguments, folder_path))
