import cv2
import os
import numpy as np
import re


def find_usb_camera():
    # Aktualnie trzeba wyłączyć kamerę systemową przed uruchomieniem przechwytywanie, żeby przechwycić poprawną kamerę
    for i in range(5):  # Assuming there are at most 2 potential cameras
        cap = cv2.VideoCapture(i, cv2.CAP_DSHOW)
        if cap is None or not cap.isOpened():
            cap.release()
            continue
        cap.release()
        return i

    return None  # If no USB camera was found


def is_image_cut_off(image_path, target_color=(129, 127, 130), threshold=2):
    image = cv2.imread(image_path)
    for i in range(2):  # Checking first two rows
        for j in range(len(image[i])):
            current_color_diff = np.abs(image[i][j] - target_color)
            if not np.all(current_color_diff <= threshold, axis=-1).all():
                return False
    return True


def highest_number_in_folder():
    pattern = r"photo \((\d+)\)"  # pattern with capturing group for the number
    numbers = []

    for file in os.listdir("photos"):
        match = re.search(pattern, file)  # Use regex search instead of "in"
        if match:
            number = int(match.group(1))  # extract number using group
            numbers.append(number)

    if numbers:
        return max(numbers)
    return 0


def next_filename():
    number = highest_number_in_folder() + 1
    return f"photo ({number}).jpg"


def delete_last_photo():
    highest_number = highest_number_in_folder()
    if highest_number:
        file_to_delete = os.path.join("photos", f"photo ({highest_number}).jpg")
        if os.path.exists(file_to_delete):
            os.remove(file_to_delete)


def set_props(camera_index):
    cap = cv2.VideoCapture(camera_index, cv2.CAP_DSHOW)
    cap.set(cv2.CAP_PROP_BRIGHTNESS, 50.0)
    cap.set(cv2.CAP_PROP_CONTRAST, 50.0)

    print("brightness:", cap.get(cv2.CAP_PROP_BRIGHTNESS))
    print("contrast:", cap.get(cv2.CAP_PROP_CONTRAST))

    cap.release()
    return


def capture_photo():
    if not os.path.exists("photos"):
        os.makedirs("photos")

    camera_index = find_usb_camera()
    if camera_index is None:
        print("No operational USB camera found.")
        return

    # set_props(camera_index)
    cap = cv2.VideoCapture(camera_index, cv2.CAP_DSHOW)
    cap.set(cv2.CAP_PROP_FRAME_WIDTH, 1920)
    cap.set(cv2.CAP_PROP_FRAME_HEIGHT, 1080)

    while True:
        ret, frame = cap.read()

        if ret:
            path_to_save = os.path.join("photos", next_filename())
            cv2.imwrite(path_to_save, frame)
            if is_image_cut_off(path_to_save):
                print("Detected a cut-off image. Retaking the photo.")
                delete_last_photo()
            else:
                print("Photo captured successfully.")
                break

    cap.release()


if __name__ == "__main__":
    capture_photo()
