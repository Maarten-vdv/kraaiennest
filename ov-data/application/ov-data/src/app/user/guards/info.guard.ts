import {CanActivateFn, Router} from '@angular/router';
import {inject} from "@angular/core";

export const infoGuard: CanActivateFn = (route, state) => {
  return route.queryParamMap.has("code") ?? inject(Router).createUrlTree(["not-found"])
};
