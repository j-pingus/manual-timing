import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PoolConfigComponent } from './pool-config.component';

describe('PoolConfigComponent', () => {
  let component: PoolConfigComponent;
  let fixture: ComponentFixture<PoolConfigComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PoolConfigComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PoolConfigComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
