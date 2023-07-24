import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { infoGuard } from './info.guard';

describe('infoGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => infoGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
