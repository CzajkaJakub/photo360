import {Component} from '@angular/core';
import {ToastComponent} from "../../general/toast/toast.component";
import {ImageUploaderService} from "../../general/data/image.uploader.service";

@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.css']
})
export class HomePageComponent {

  pickedGif!: any
  gifs: any[] = []

  constructor(private toast: ToastComponent,
              private imageUploaderService: ImageUploaderService) {
  }

  handleSelectionChange() {
    if (this.pickedGif == "private") {
      this.imageUploaderService.fetchPrivateGifs().subscribe(gifData => {
        this.gifs.splice(0, this.gifs.length);
        gifData.forEach(gifEl => {
          this.gifs.push('data:image/gif;base64,' + gifEl.gif)
        })
      })
    } else if (this.pickedGif == "public") {
      this.imageUploaderService.fetchPublicGifs().subscribe(gifData => {
        this.gifs.splice(0, this.gifs.length);
        gifData.forEach(gifEl => {
          this.gifs.push('data:image/gif;base64,' + gifEl.gif)
        })
      })
    } else {
      this.imageUploaderService.fetchGif(this.pickedGif).subscribe(gifData => {
        this.gifs.splice(0, this.gifs.length);
        this.gifs.push('data:image/gif;base64,' + gifData.gif)
      })
    }
  }
}
