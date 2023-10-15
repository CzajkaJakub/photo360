import {Component} from '@angular/core';
import {ToastComponent} from "../../general/toast/toast.component";
import {ImageUploaderService} from "../../general/data/image.uploader.service";
import {GifService} from "../../general/data/gif.service";

@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.css']
})
export class HomePageComponent {

  pickedGif!: any
  gifs: any[] = []

  constructor(private toast: ToastComponent,
              private imageUploaderService: ImageUploaderService, private gifService: GifService) {
  }

  handleSelectionChange() {
    if (this.pickedGif == "private") {
      this.gifService.fetchPrivateGifs().subscribe(gifData => {
        this.gifs.splice(0, this.gifs.length);
        gifData.forEach(gifEl => {
          this.gifs.push('data:image/gif;base64,' + gifEl.gif)
        })
      })
    } else if (this.pickedGif == "public") {
      this.gifService.fetchPublicGifs().subscribe(gifData => {
        this.gifs.splice(0, this.gifs.length);
        gifData.forEach(gifEl => {
          this.gifs.push('data:image/gif;base64,' + gifEl.gif)
        })
      })
    } else {
      this.gifService.fetchGif(this.pickedGif).subscribe(gifData => {
        this.gifs.splice(0, this.gifs.length);
        this.gifs.push('data:image/gif;base64,' + gifData.gif)
      })
    }
  }
}
