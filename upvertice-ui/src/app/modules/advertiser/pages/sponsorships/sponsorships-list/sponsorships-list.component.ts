import {Component, OnInit} from '@angular/core';
import {SponsorshipControllerService} from '../../../../../services/services/sponsorship-controller.service';
import {Router} from '@angular/router';
import {SponsorshipResponse} from '../../../../../services/models/sponsorship-response';
import {
  GetSponsorshipsByStatus$Params
} from '../../../../../services/fn/sponsorship-controller/get-sponsorships-by-status';

@Component({
  selector: 'app-sponsorships-list',
  standalone: false,
  templateUrl: './sponsorships-list.component.html',
  styleUrls: ['./sponsorships-list.component.scss']
})
export class SponsorshipsListComponent implements OnInit {
  sponsorships: SponsorshipResponse[] = [];
  selectedStatus: string = '';
  page = 0;
  size = 5;
  totalPages = 0;


  constructor(
    private sponsorshipService: SponsorshipControllerService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadSponsorships();
  }

  statusFilter: string = ''; // default is all
  onStatusChange(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.statusFilter = value as GetSponsorshipsByStatus$Params['status'] | '';
    this.page = 0;
    this.loadSponsorships();
  }
  loadSponsorships(): void {
    const params = {
      page: this.page,
      size: this.size
    };

    const observable = this.statusFilter
      ? this.sponsorshipService.getSponsorshipsByStatus({
        ...params,
        status: this.statusFilter as GetSponsorshipsByStatus$Params['status']
      })
      : this.sponsorshipService.getAllSponsorships(params);

    observable.subscribe({
      next: res => {
        this.sponsorships = res.content ?? [];
        this.totalPages = res.totalPages ?? 0;
      }
    });
  }
  goToDetails(id: number | undefined) {
    if (id) this.router.navigate(['/advertiser/sponsorships', id]);
  }

  filterByStatus() {
    this.page = 0;
    this.loadSponsorships();
  }

  nextPage() {
    if (this.page + 1 < this.totalPages) {
      this.page++;
      this.loadSponsorships();
    }
  }

  prevPage() {
    if (this.page > 0) {
      this.page--;
      this.loadSponsorships();
    }
  }

}
