import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdvertiserComponent } from './advertiser.component';

describe('AdvertiserComponent', () => {
  let component: AdvertiserComponent;
  let fixture: ComponentFixture<AdvertiserComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AdvertiserComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdvertiserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
