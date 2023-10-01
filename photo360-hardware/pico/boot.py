import network
import time
import socket
import sys
from servo import Servo


my_servo = Servo(pin_id=16) 	# Create Servo object
delay_ms = 25  					# Amount of milliseconds to wait between servo movements


def make_move(start_deg, stop_deg, delay_ms=25):
    move_array = list(range(start_deg, stop_deg + 1)) if start_deg <= stop_deg else list(reversed(range(stop_deg, start_deg + 1)))  
    for position in move_array:
        print(position)				# Show the current position of the Servo
        my_servo.write(position)  	# Set the Servo to the current position
        time.sleep_ms(delay_ms)  	# Wait for the servo to make the movement


def calibration():
    my_servo.write(0)				# Goining to starting position
    time.sleep_ms(3000)
    make_move(0, 360)
    make_move(360, 0)


def single_move(target_deg, wait_time_ms):
    make_move(0, target_deg)
    time.sleep_ms(wait_time_ms)
    make_move(target_deg, 0)
            

def full_move_every_few_degree(step_deg, wait_time_ms):
    if 360 % step_deg:
        print("Move with this step isn't possible!")
        return
    
    steps = [step_deg * i for i in range(360 // step_deg + 1)]
    print(len(steps))
    print("List of steps: ", steps)
    for i in range(len(steps) - 1):
        make_move(steps[i], steps[i+1])
        time.sleep_ms(wait_time_ms)
    make_move(360, 0)


# def connect_to_internet(ssid, password):
#     # Pass in string arguments for ssid and password
# 
#     # Just making our internet connection
#     wlan = network.WLAN(network.STA_IF)
#     wlan.active(True)
#     wlan.connect(ssid, password)
# 
#     # Wait for connect or fail
#     max_wait = 10
#     while max_wait > 0:
#         if wlan.status() < 0 or wlan.status() >= 3:
#             break
#         max_wait -= 1
#         print('waiting for connection...')
#         time.sleep(1)
#     # Handle connection error
#     if wlan.status() != 3:
#         raise RuntimeError('network connection failed')
#     else:
#         print('connected')
#         status = wlan.ifconfig()
#         print(status[0])


# if you do not see the network you may have to power cycle
# unplug your pico w for 10 seconds and plug it in again
def ap_mode(ssid, password):
    """
        Description: This is a function to activate AP mode
        
        Parameters:
        
        ssid[str]: The name of your internet connection
        password[str]: Password for your internet connection
        
        Returns: Nada
    """
    # Just making our internet connection
    ap = network.WLAN(network.AP_IF)
    ap.config(essid=ssid, password=password)
    ap.active(True)
    
    while ap.active() == False:
        pass
    
    print('AP Mode Is Active, You can Now Connect')
    print('IP Address To Connect to: ' + ap.ifconfig()[0])
   

def command_logic(command):
    params = command.strip().split(" ")
    if params[0] == "single_move":
        target_deg = 90
        wait_time_ms = 3000
        if len(params) > 1:
            target_deg = int(params[1])
        if len(params) > 2:
            wait_time_ms = int(params[2])
        single_move(target_deg, wait_time_ms)
        return 'completed'
    elif params[0] == 'full_move':
        step_deg = 90
        wait_time_ms = 3000
        if len(params) > 1:
            step_deg = int(params[1])
        if len(params) > 2:
            wait_time_ms = int(params[2])
        full_move_every_few_degree(step_deg, wait_time_ms)
        return 'completed'
    else:
        return message + ' hasn\'t been recognized'      
      
def main():
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)   #creating socket object
    server_socket.bind(('192.168.4.1', 80))
    server_socket.listen(5)
    
    print('Waiting for connections...')
    
    while True:
        client_socket, client_address = server_socket.accept()
        print('Got a connection from %s' % str(client_address))
        request = client_socket.recv(1024)
        
        # Handle the received command
        command = request.decode()
        print('Received command:', command)
      
        # Perform actions based on the command (implement your logic here)
        response = command_logic(command)
    
      
        # Send a response (if needed)
        client_socket.send(response.encode())
        client_socket.close()
      

if __name__ == "__main__":
#    connect_to_internet("SSID", "password")
    ap_mode('PICO_W_AP', 'pico_w_servo')
    calibration()
    main()