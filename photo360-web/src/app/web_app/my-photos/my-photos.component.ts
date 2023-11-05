import {Component, OnDestroy, OnInit} from '@angular/core';
import {ToastComponent} from "../../general/toast/toast.component";
import {ImageUploaderService} from "../../general/data/image.uploader.service";
import {GifService} from "../../general/data/gif.service";
import {User} from "../../general/auth/user-mode";
import {Subscription} from "rxjs";
import {AuthService} from "../../general/auth/auth.service";

@Component({
  selector: 'app-my-photos',
  templateUrl: './my-photos.component.html',
  styleUrls: ['./my-photos.component.css']
})
export class MyPhotosComponent implements OnInit, OnDestroy{

  gifs: any[] = []

  protected isAuthenticated = false;
  private userSubscription!: Subscription;
  private translationSubscription!: Subscription;
  protected currentlyLoggedUser!: User

  constructor(private toast: ToastComponent,
              public authService: AuthService,
              private imageUploaderService: ImageUploaderService, private gifService: GifService) {
  }

  ngOnInit(): void {
    this.userSubscription = this.authService.user.subscribe(user => {
      this.isAuthenticated = !!user;
      if (this.isAuthenticated) {
        this.currentlyLoggedUser = user
      }
    });
    this.gifService.fetchPrivateGifs().subscribe(gifDataList => {
      this.gifs = gifDataList;
      console.log(gifDataList);
    });
  }

  ngOnDestroy() {
    this.userSubscription.unsubscribe();
  }
}
