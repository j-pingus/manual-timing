import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrintEventComponent } from './print-event.component';

describe('PrintEventComponent', () => {
  let component: PrintEventComponent;
  let fixture: ComponentFixture<PrintEventComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PrintEventComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PrintEventComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
