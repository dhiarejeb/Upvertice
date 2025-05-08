import {Component} from '@angular/core';
import {SponsorAdResponse} from '../../../../services/models/sponsor-ad-response';
import {SponsorAdControllerService} from '../../../../services/services/sponsor-ad-controller.service';
import {ToastrService} from 'ngx-toastr';


@Component({
  selector: 'app-sponsor-ads',
  standalone: false,
  templateUrl: './sponsor-ads.component.html',
  styleUrls: ['./sponsor-ads.component.scss']
})
export class SponsorAdsProviderComponent {
  sponsorAds: SponsorAdResponse[] = [];
  page = 0;
  size = 10;
  totalPages = 0;
  pageInput = 1;

  constructor(
    private sponsorAdService: SponsorAdControllerService,
    private toastService: ToastrService // Inject toastr service
  ) {}

  ngOnInit(): void {
    this.loadSponsorAds();
  }

  loadSponsorAds(): void {
    this.sponsorAdService.getAllSponsorAds({ page: this.page, size: this.size }).subscribe({
      next: (res) => {
        this.sponsorAds = res.content ?? [];
        this.totalPages = res.totalPages ?? 0;
        this.pageInput = this.page + 1;
        this.toastService.success('Sponsor ads loaded successfully!'); // Success message on successful load
      },
      error: (err) => {
        console.error('Failed to load sponsor ads', err);
        this.toastService.error('Failed to load sponsor ads. Please try again later.'); // Error message on failure
      },
    });
  }

  nextPage(): void {
    if (this.page + 1 < this.totalPages) {
      this.page++;
      this.loadSponsorAds();
    }
  }

  prevPage(): void {
    if (this.page > 0) {
      this.page--;
      this.loadSponsorAds();
    }
  }

  goToPage(page: number): void {
    const newPage = page - 1;
    if (newPage >= 0 && newPage < this.totalPages) {
      this.page = newPage;
      this.loadSponsorAds();
    }
  }
}


