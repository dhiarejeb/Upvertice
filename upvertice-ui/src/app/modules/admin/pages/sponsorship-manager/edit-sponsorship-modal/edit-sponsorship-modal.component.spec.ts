import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditSponsorshipModalComponent } from './edit-sponsorship-modal.component';

describe('EditSponsorshipModalComponent', () => {
  let component: EditSponsorshipModalComponent;
  let fixture: ComponentFixture<EditSponsorshipModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EditSponsorshipModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditSponsorshipModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
