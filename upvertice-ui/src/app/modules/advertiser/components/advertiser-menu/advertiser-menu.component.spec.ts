import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdvertiserMenuComponent } from './advertiser-menu.component';

describe('AdvertiserMenuComponent', () => {
  let component: AdvertiserMenuComponent;
  let fixture: ComponentFixture<AdvertiserMenuComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AdvertiserMenuComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdvertiserMenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
