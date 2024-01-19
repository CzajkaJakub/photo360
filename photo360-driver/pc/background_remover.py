import os
import subprocess


def remove_background(directory):
    for root, dirs, files in os.walk(directory):
        for file in files:
            if file.lower().endswith(('.jpg', '.jpeg')):
                file_path = os.path.join(root, file)
                process_image(file_path)

    # Usuwanie plików .jpg
    delete_jpg_files(directory)


def process_image(file_path):
    new_file_path = file_path.rsplit('.', 1)[0] + '.png'
    try:
        subprocess.run(['backgroundremover', '-i', file_path, '-o', new_file_path], check=True)
    except Exception as e:
        return


def delete_jpg_files(directory):
    for root, dirs, files in os.walk(directory):
        for file in files:
            if file.lower().endswith(('.jpg', '.jpeg')):
                os.remove(os.path.join(root, file))


if __name__ == "__main__":
    directory = 'F:/kapibara_photo_3_test'  # Zmień na odpowiednią ścieżkę
    remove_background(directory)
