import socket


def send_command(p_socket, p_params, command):
    # Connect to the Pico
    p_socket.connect((p_params.get("pico_ip"), p_params.get("pico_port")))

    # Send a command to the Pico
    p_socket.send(command.encode())

    # Receive the response
    response = p_socket.recv(1024)
    print('Response from Pico:', response.decode())

    # Close the socket when done
    p_socket.close()


def main():
    # Raspberry Pi Pico W's IP address and port
    pico_params = {
        "pico_ip": '192.168.4.1',
        "pico_port": 80
    }

    pico_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    send_command(pico_socket, pico_params, "full_move")
    send_command(pico_socket, pico_params, "full_move 180")
    send_command(pico_socket, pico_params, "single_move 30 1000")
    send_command(pico_socket, pico_params, "single_move 45")
    send_command(pico_socket, pico_params, "single_move")
    send_command(pico_socket, pico_params, "full_move 45 1500")


if __name__ == "__main__":
    main()


