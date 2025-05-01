import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {AdminComponent} from './admin/admin.component';
import {HomeComponent} from './pages/home/home.component';
import {ManagerComponent} from './pages/manager/manager.component';
import {DashboardComponent} from './pages/dashboard/dashboard.component';
import {authGuard} from '../../core/authGuard/auth-guard.service';


const routes: Routes = [

];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdminRoutingModule {}
