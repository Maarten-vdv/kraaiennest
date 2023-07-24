import { TestBed } from '@angular/core/testing';
import { ResolveFn } from '@angular/router';

import { infoResolver } from './info.resolver';

describe('infoResolver', () => {
  const executeResolver: ResolveFn<boolean> = (...resolverParameters) => 
      TestBed.runInInjectionContext(() => infoResolver(...resolverParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeResolver).toBeTruthy();
  });
});
