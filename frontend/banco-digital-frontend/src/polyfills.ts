/**
 * This file includes polyfills needed for an Angular application.
 */

import 'zone.js';  // Included with Angular CLI.
(window as any).global = window; // Adiciona polyfill para 'global'
(window as any).Buffer = (window as any).Buffer || require('buffer').Buffer; // Adiciona polyfill para Buffer