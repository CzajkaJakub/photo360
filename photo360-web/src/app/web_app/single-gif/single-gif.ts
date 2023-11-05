import {Component, OnDestroy, OnInit} from '@angular/core';
import {ToastComponent} from "../../general/toast/toast.component";
import {ImageUploaderService} from "../../general/data/image.uploader.service";
import {GifService} from "../../general/data/gif.service";
import {User} from "../../general/auth/user-mode";
import {Subscription} from "rxjs";
import {AuthService} from "../../general/auth/auth.service";
import {ActivatedRoute, Router} from "@angular/router";
import {format} from 'date-fns';
import {utcToZonedTime} from 'date-fns-tz';
import { Location } from '@angular/common';
import {GitDataDto, RequestResponse} from "../../general/interface/interface";
import * as JSZip from "jszip";
import { saveAs } from 'file-saver';
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'app-single-gif',
  templateUrl: './single-gif.html',
  styleUrls: ['./single-gif.css']
})
export class SingleGifComponent implements OnInit, OnDestroy{

  gifId: number = 0;
  currentIndex: number = 0;

  isLoading = true;

  gif: GitDataDto = {
    gif: '',
    gifId: 0,
    userLogin: '',
    isPublic: true,
    title: '',
    description: '',
    uploadDateTime: 0,
    listOfPhotos: [],
    headPhoto: ''
  }

  protected isAuthenticated = false;
  private userSubscription!: Subscription;
  protected currentlyLoggedUser!: User

  constructor(private toast: ToastComponent,
              public authService: AuthService,
              private imageUploaderService: ImageUploaderService, private gifService: GifService, private route: ActivatedRoute, private location: Location, private router: Router, private translate: TranslateService) {
  }

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      this.gifId = +params['id'];
    });

    this.userSubscription = this.authService.user.subscribe(user => {
      this.isAuthenticated = !!user;
      if (this.isAuthenticated) {
        this.currentlyLoggedUser = user
      }
    });

    this.gifService.fetchGif(this.gifId).subscribe(gifData => {
      this.gif = gifData;
      this.isLoading = false;
    });
  }

  formatDateTime(dateTime: number): string {
    const date = new Date(dateTime);
    const zonedDate = utcToZonedTime(date, 'UTC');
    return format(zonedDate, 'dd.MM.yyy');
  }

  goBack(): void {
    this.location.back();
  }

  isGifVisible(): boolean {
    return this.currentIndex === 0;
  }

  getCurrentPhoto(): string {
    if (this.gif.listOfPhotos && this.gif.listOfPhotos.length > 0) {
      const index = (this.currentIndex - 1 + this.gif.listOfPhotos.length) % this.gif.listOfPhotos.length;
      return 'data:image/jpeg;base64,' + this.gif.listOfPhotos[index];
    }
    return '';
  }

  navigate(offset: number): void {
    const newIndex = (this.currentIndex + offset + this.gif.listOfPhotos.length) % this.gif.listOfPhotos.length;
    this.currentIndex = (newIndex + this.gif.listOfPhotos.length) % this.gif.listOfPhotos.length;
    console.log(this.currentIndex);
  }

  addToFavourites(gifData: GitDataDto): void {
    this.gifService.addGifToFavourite(gifData).subscribe(response => {
      this.toast.openSnackBar(this.translate.instant('single-gif.addFavouritesSuccess'));
    }, error => {
      this.toast.openSnackBar(this.translate.instant('single-gif.addFavouritesError'));
    });
  }

  downloadProject(gifData: GitDataDto): void {
    // npm install jszip
    // npm install file-saver
    const zip = new JSZip();
    zip.file(`gif-${gifData.gifId}.gif`, gifData.gif, { base64: true });
    gifData.listOfPhotos.forEach((photo, index) => {
      zip.file(`photo-${index + 1}.jpg`, photo, { base64: true });
    });
    zip.generateAsync({ type: 'blob' }).then((content) => {
      saveAs(content, `project-${gifData.title}.zip`);
      this.toast.openSnackBar(this.translate.instant('single-gif.downloadSuccess'));
    });
  }

  deleteProject(gifData: GitDataDto): void {
    this.gifService.removeGif(gifData).subscribe(response => {
      this.toast.openSnackBar(this.translate.instant('single-gif.deleteSuccess'));
      this.router.navigate(['/my-photos']);
    }, error => {
      this.toast.openSnackBar(this.translate.instant('single-gif.deleteError'));
    });
  }

  ngOnDestroy() {
    this.userSubscription.unsubscribe();
  }
}
