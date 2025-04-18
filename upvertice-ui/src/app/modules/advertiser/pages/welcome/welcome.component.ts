import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'app-welcome',
  standalone: false,
  templateUrl: './welcome.component.html',
  styleUrls: ['./welcome.component.scss']
})
export class WelcomeComponent implements OnInit {
    ngOnInit(): void {
      console.log('WelcomeComponent loaded');

    }


}
