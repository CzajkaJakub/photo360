import network
import time
import socket
import sys
from servo import Servo
from machine import Pin 
import bluetooth
from ble_simple_peripheral import BLESimplePeripheral


minimal_wait_time_ms = 500		# Set up the minimal wait time for servo
my_servo = Servo(pin_id=16) 	# Create Servo object
delay_ms = 25					# Amount of milliseconds to wait between servo movements
ble = bluetooth.BLE()			# Create a Bluetooth Low Energy (BLE) object
sp = BLESimplePeripheral(ble)	# Create an instance of the BLESimplePeripheral class with the BLE object
com = None


def make_move(start_deg, stop_deg, delay_ms=25):
    move_array = list(range(start_deg, stop_deg + 1)) if start_deg <= stop_deg else list(reversed(range(stop_deg, start_deg + 1)))  
    for position in move_array:
#        print(position)				# Show the current position of the Servo
        my_servo.write(position)	# Set the Servo to the current position
        time.sleep_ms(delay_ms)		# Wait for the servo to make the movement


def calibration():
    my_servo.write(0)				# Goining to starting position
    time.sleep_ms(3000)
    make_move(0, 360)
    make_move(360, 0)


def single_move(target_deg, wait_time_ms):
    if target_deg < 1:
        return "Move with this step isn't possible!"
    if wait_time_ms < minimal_wait_time_ms:
        return "Wait time is less than minimal available wait time!"
    
    make_move(0, target_deg)
    time.sleep_ms(wait_time_ms)
    make_move(target_deg, 0)
    return "Completed"
            

def full_move_every_few_degree(step_deg, wait_time_ms):
    if 360 % step_deg or step_deg < 1:
        return "Move with this step isn't possible!"
    if wait_time_ms < minimal_wait_time_ms:
        return "Wait time is less than minimal available wait time!"
    
    steps = [step_deg * i for i in range(360 // step_deg + 1)]
    for i in range(len(steps) - 1):
        make_move(steps[i], steps[i+1])
        time.sleep_ms(wait_time_ms)
    make_move(360, 0)
    return "Completed"


def write_command(data):
    global com
    com = data.decode()


def command_logic(command):
#    sp.send("Received command: " + command)
    print("Received command:", command)
    
    params = command.strip().split(" ")
    if params[0] == "single_move":
        target_deg = 90
        wait_time_ms = 3000
        if len(params) > 1:
            try:
                target_deg = int(params[1])
            except ValueError:
                sp.send("Command: " + command + " - Invalid argument provided!")
                return
        if len(params) > 2:
            try:
                wait_time_ms = int(params[2])
            except ValueError:
                sp.send("Command: " + command + " - Invalid argument provided!")
                return
        response = single_move(target_deg, wait_time_ms)
        sp.send("Command: " + command + " - " + response)
    elif params[0] == 'full_move':
        step_deg = 90
        wait_time_ms = 3000
        if len(params) > 1:
            try:
                step_deg = int(params[1])
            except ValueError:
                sp.send("Command: " + command + " - Invalid argument provided!")
                return
        if len(params) > 2:
            try:
                wait_time_ms = int(params[2])
            except ValueError:
                sp.send("Command: " + command + " - Invalid argument provided!")
                return
        response = full_move_every_few_degree(step_deg, wait_time_ms)
        sp.send("Command: " + command + " - " + response)
    else:
        sp.send("Command: " + command + " - hasn't been recognized")      
      
      
def main():  
    # Start an infinite loop
    while True:
        global com
        if sp.is_connected():  			# Check if a BLE connection is established
            sp.on_write(write_command)  # Set the callback function for data reception
            if com != None:
                command_logic(com)
                com = None
      

if __name__ == "__main__":
    calibration()
    main()