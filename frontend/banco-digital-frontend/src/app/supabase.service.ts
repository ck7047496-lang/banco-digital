import { Injectable } from '@angular/core';
import { createClient, SupabaseClient } from '@supabase/supabase-js';
import { environment } from '../environments/environment'; // Assumindo que você tem um arquivo environment.ts

@Injectable({
  providedIn: 'root'
})
export class SupabaseService {
  private supabase: SupabaseClient;

  constructor() {
    // Substitua com suas credenciais do Supabase
    const supabaseUrl = 'https://abdjlaacijodbqylbbxj.supabase.co';
    const supabaseAnonKey = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImFiZGpsYWFjaWpvZGJxeWxiYnhqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MTk1OTQ2MjYsImV4cCI6MTczNTU2NjYyNn0.LpKM2Cv2LRduGhCfLLlEdRi7O7LUy79vaJV84iKg5F0';

    this.supabase = createClient(supabaseUrl, supabaseAnonKey);
  }

  get client(): SupabaseClient {
    return this.supabase;
  }
}