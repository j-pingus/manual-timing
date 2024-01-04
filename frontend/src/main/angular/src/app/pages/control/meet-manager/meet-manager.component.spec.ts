import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MeetManagerComponent } from './meet-manager.component';

describe('MeetManagerComponent', () => {
  let component: MeetManagerComponent;
  let fixture: ComponentFixture<MeetManagerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MeetManagerComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(MeetManagerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
