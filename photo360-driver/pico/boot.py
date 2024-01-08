import network
import time
import socket
import sys
from servo import Servo
from machine import Pin
import bluetooth
from ble_simple_peripheral import BLESimplePeripheral

my_servo = Servo(pin_id=16)  # Create Servo object
delay_ms = 25  # Amount of milliseconds to wait between servo movements
ble = bluetooth.BLE()  # Create a Bluetooth Low Energy (BLE) object
sp = BLESimplePeripheral(ble)  # Create an instance of the BLESimplePeripheral class with the BLE object
com = None
next_m = None


def wait_for_photo():
    global next_m
    sp.send("PHOTO")
    while True:
        if sp.is_connected():  # Check if a BLE connection is established
            sp.on_write(get_next_m)  # Set the callback function for data reception
            if next_m != None:
                next_m = None
                return


def make_move(start_deg, stop_deg):
    global delay_ms
    move_array = list(range(start_deg, stop_deg + 1)) if start_deg <= stop_deg else list(
        reversed(range(stop_deg, start_deg + 1)))
    for position in move_array:
        my_servo.write(position)  # Set the Servo to the current position
        time.sleep_ms(delay_ms)  # Wait for the servo to make the movement


def calibration():
    my_servo.write(0)  # Goining to starting position
    time.sleep_ms(3000)
    make_move(0, 360)
    make_move(360, 0)


def single_move(target_deg):
    if target_deg < 0 or target_deg > 360:
        return "Move with this step isn't possible!"

    make_move(0, target_deg)
    wait_for_photo()
    make_move(target_deg, 0)

    return "Completed"


def full_move_every_few_degree(step_deg):
    if 360 % step_deg or step_deg < 0 or step_deg > 360:
        return "Move with this step isn't possible!"

    steps = [step_deg * i for i in range(360 // step_deg + 1)]
    for i in range(len(steps) - 1):
        make_move(steps[i], steps[i + 1])
        wait_for_photo()

    make_move(360, 0)
    return "Completed"


def get_next_m(data):
    global next_m
    next_m = data.decode()


def write_command(data):
    global com
    com = data.decode()


def command_logic(command):
    params = command.strip().split(" ")
    if len(params) != 2:
        sp.send("Command: " + command + " - Excpected two arguments!")
        return

    if params[0] == "single_move":
        try:
            response = single_move(int(params[1]))
            sp.send("Command: " + command + " - " + response)
        except ValueError:
            sp.send("Command: " + command + " - Invalid argument provided!")
    elif params[0] == 'full_move':
        try:
            response = full_move_every_few_degree(int(params[1]))
            sp.send("Command: " + command + " - " + response)
        except ValueError:
            sp.send("Command: " + command + " - Invalid argument provided!")
    else:
        sp.send("Command: " + command + " - hasn't been recognized")


def main():
    while True:
        global com
        if sp.is_connected():  # Check if a BLE connection is established
            sp.on_write(write_command)  # Set the callback function for data reception
            if com != None:
                command_logic(com)
                com = None


if __name__ == "__main__":
    calibration()
    main()
