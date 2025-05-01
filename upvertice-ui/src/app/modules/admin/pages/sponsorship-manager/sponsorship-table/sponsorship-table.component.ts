import { Component, Input, Output, EventEmitter } from '@angular/core';
import {SponsorshipResponse} from '../../../../../services/models/sponsorship-response';

@Component({
  selector: 'app-sponsorship-table',
  standalone: false,
  templateUrl: './sponsorship-table.component.html',
  styleUrl: './sponsorship-table.component.scss'
})
export class SponsorshipTableComponent {
  @Input() sponsorships: SponsorshipResponse[] = [];
  @Output() edit = new EventEmitter<SponsorshipResponse>();
  @Output() delete = new EventEmitter<SponsorshipResponse>();

  displayedColumns: string[] = ['id', 'userId', 'offerTitle', 'status', 'createdDate', 'designStatus', 'actions'];


  getStatusClass(status: string | undefined): string {
    if (!status) return '';

    switch (status) {
      case 'APPROVED': return 'status-approved';
      case 'PENDING': return 'status-pending';
      case 'REJECTED': return 'status-rejected';
      case 'FINISHED': return 'status-finished';
      default: return '';
    }
  }

  onEdit(sponsorship: SponsorshipResponse): void {
    this.edit.emit(sponsorship);
  }

  onDelete(sponsorship: SponsorshipResponse): void {
    this.delete.emit(sponsorship);
  }
}
