import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyProvidershipComponent } from './my-providership.component';

describe('MyProvidershipComponent', () => {
  let component: MyProvidershipComponent;
  let fixture: ComponentFixture<MyProvidershipComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MyProvidershipComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MyProvidershipComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
