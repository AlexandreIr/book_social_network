// app.config.server.ts
import { ApplicationConfig, mergeApplicationConfig } from '@angular/core';
import {provideClientHydration, withNoHttpTransferCache} from '@angular/platform-browser';
import { appConfig } from './app.config';

const serverConfig: ApplicationConfig = {
  providers: [
    provideClientHydration(withNoHttpTransferCache())
  ]
};

export const config = mergeApplicationConfig(appConfig, serverConfig);
