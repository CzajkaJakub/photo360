import {Component} from '@angular/core';
import {ToastComponent} from "../../general/toast/toast.component";
import {ImageUploaderService} from "../../general/data/image.uploader.service";
import {DomSanitizer, SafeUrl} from "@angular/platform-browser";

@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.css']
})
export class HomePageComponent {

  gifUrl!: SafeUrl;
  pickedGif!: number

  constructor(private toast: ToastComponent,
              private imageUploaderService: ImageUploaderService,
              private sanitizer: DomSanitizer) {
  }

  handleSelectionChange() {
    this.imageUploaderService.fetchGif(this.pickedGif).subscribe(gifData => {
      this.gifUrl = this.sanitizer.bypassSecurityTrustUrl(URL.createObjectURL(gifData));
    })
  }
}
