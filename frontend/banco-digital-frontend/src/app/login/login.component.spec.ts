import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { Router } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';

import { LoginComponent } from './login.component';
import { UsuarioService } from '../usuario.service';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let mockUsuarioService: any;
  let mockRouter: any;

  beforeEach(async () => {
    mockUsuarioService = {
      login: jasmine.createSpy('login').and.returnValue(of({ token: 'fake-token', tipoUsuario: 'GERENTE' }))
    };
    mockRouter = {
      navigate: jasmine.createSpy('navigate')
    };

    await TestBed.configureTestingModule({
      imports: [LoginComponent, FormsModule, ReactiveFormsModule],
      providers: [
        { provide: UsuarioService, useValue: mockUsuarioService },
        { provide: Router, useValue: mockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    component.ngOnInit(); // Initialize the form
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should navigate to painel-gerente on successful manager login', fakeAsync(() => {
    component.loginForm.controls['email'].setValue('gerente@example.com');
    component.loginForm.controls['senha'].setValue('password123');
    component.userType = 'gerente'; // Set userType for the test
    component.onSubmit();

    tick();

    expect(mockUsuarioService.login).toHaveBeenCalledWith('gerente@example.com', 'password123', 'gerente');
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/painel-gerente']);
  }));

  it('should display error message on failed login', fakeAsync(() => {
    mockUsuarioService.login.and.returnValue(throwError({ status: 401 }));

    component.loginForm.controls['email'].setValue('invalid@example.com');
    component.loginForm.controls['senha'].setValue('wrongpassword');
    component.userType = 'cliente'; // Set userType for the test
    component.onSubmit();

    tick();

    expect(component.errorMessage).toBe('Credenciais inválidas ou cadastro pendente.'); // Updated error message
    expect(mockRouter.navigate).not.toHaveBeenCalled();
  }));
});
