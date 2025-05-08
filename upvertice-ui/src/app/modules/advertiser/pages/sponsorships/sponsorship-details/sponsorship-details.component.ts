import { Component, type OnInit, ViewChild } from "@angular/core"
import { ChartConfiguration, ChartData, ChartType } from "chart.js"
import { BaseChartDirective } from "ng2-charts"
import { ActivatedRoute } from "@angular/router"
import { SponsorshipResponse } from "../../../../../services/models/sponsorship-response"
import { SponsorshipControllerService } from "../../../../../services/services/sponsorship-controller.service"
import { ProvidershipLightResponse } from "../../../../../services/models/providership-light-response"
import { SupplierTransactionLightResponse } from "../../../../../services/models/supplier-transaction-light-response"
import { forkJoin } from "rxjs"

// Register required Chart.js components
import { Chart, registerables } from "chart.js"
import {ToastrService} from 'ngx-toastr';
Chart.register(...registerables)

@Component({
  selector: "app-sponsorship-details",
  standalone : false,
  templateUrl: "./sponsorship-details.component.html",
  styleUrls: ["./sponsorship-details.component.scss"],
})
export class SponsorshipDetailsComponent implements OnInit {
  @ViewChild(BaseChartDirective) chart: BaseChartDirective | undefined;

  sponsorship?: SponsorshipResponse;
  loading = true;
  loadingCharts = true;
  activeTab = 'overview';

  // Production metrics
  totalProduction = 0;
  completedProduction = 0;
  productionPercentage = 0;

  // Sales metrics
  totalSales = 0;
  totalAvailable = 0;
  salesPercentage = 0;

  // Provider chart
  providerChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: true,
        position: 'top',
      },
      tooltip: {
        mode: 'index',
        intersect: false,
      },
      title: {
        display: true,
        text: 'Production by Provider',
      },
    },
    scales: {
      y: {
        beginAtZero: true,
        title: {
          display: true,
          text: 'Production Progress (%)',
        },
      },
    },
  };

  providerChartData: ChartData<'bar'> = {
    labels: [],
    datasets: [
      {
        data: [],
        label: 'Production Progress',
        backgroundColor: [
          'rgba(66, 133, 244, 0.7)',
          'rgba(219, 68, 55, 0.7)',
          'rgba(244, 180, 0, 0.7)',
          'rgba(15, 157, 88, 0.7)',
          'rgba(98, 0, 238, 0.7)',
        ],
        borderColor: [
          'rgb(66, 133, 244)',
          'rgb(219, 68, 55)',
          'rgb(244, 180, 0)',
          'rgb(15, 157, 88)',
          'rgb(98, 0, 238)',
        ],
        borderWidth: 1,
      },
    ],
  };

  providerChartType: ChartType = 'bar';

  // Supplier chart
  supplierChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: true,
        position: 'right',
      },
      tooltip: {
        callbacks: {
          label: (context) => {
            const label = context.label || '';
            const value = context.raw as number;
            return `${label}: ${value}%`;
          },
        },
      },
      title: {
        display: true,
        text: 'Sales Distribution',
      },
    },
  };

  supplierChartData: ChartData<'doughnut'> = {
    labels: [],
    datasets: [
      {
        data: [],
        backgroundColor: [
          'rgba(66, 133, 244, 0.7)',
          'rgba(219, 68, 55, 0.7)',
          'rgba(244, 180, 0, 0.7)',
          'rgba(15, 157, 88, 0.7)',
          'rgba(98, 0, 238, 0.7)',
        ],
        hoverOffset: 4,
      },
    ],
  };

  supplierChartType: ChartType = 'doughnut';

  constructor(
    private route: ActivatedRoute,
    private sponsorshipService: SponsorshipControllerService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.loading = true;
    this.loadingCharts = true;

    const sponsorshipId = Number(this.route.snapshot.paramMap.get('id'));

    // Load sponsorship details
    this.sponsorshipService.getSponsorshipById({ sponsorshipId }).subscribe({
      next: (sponsorship) => {
        this.sponsorship = sponsorship;
        this.calculateMetrics();
        this.prepareChartData();
        this.toastr.success('Sponsorship data loaded successfully!', 'Success');
        this.loading = false;
        this.loadingCharts = false;
      },
      error: (error) => {
        console.error('Error loading sponsorship data:', error);
        this.toastr.error('Failed to load sponsorship data. Please try again.', 'Error');
        this.loading = false;
        this.loadingCharts = false;
      },
    });
  }

  calculateMetrics(): void {
    if (!this.sponsorship) return;

    // Calculate production metrics
    this.totalProduction = this.sponsorship.sponsorOffer?.productQuantity || 0;
    this.completedProduction =
      this.sponsorship.providerships?.reduce((total, p) => total + (p.producedProduct || 0), 0) || 0;

    this.productionPercentage =
      this.totalProduction > 0 ? Math.round((this.completedProduction / this.totalProduction) * 100) : 0;

    // Calculate sales metrics
    this.totalAvailable =
      this.sponsorship.supplierTransactions?.reduce(
        (total, tx) => total + (tx.supplierOffer?.quantityAvailable || 0),
        0
      ) || 0;

    this.totalSales =
      this.sponsorship.supplierTransactions?.reduce((total, tx) => total + (tx.quantitySold || 0), 0) || 0;

    this.salesPercentage = this.totalAvailable > 0 ? Math.round((this.totalSales / this.totalAvailable) * 100) : 0;
  }

  prepareChartData(): void {
    if (!this.sponsorship) return;

    // Prepare provider chart data
    if (this.sponsorship.providerships && this.sponsorship.providerships.length > 0) {
      this.providerChartData.labels = this.sponsorship.providerships.map((p) => p.location || 'Unknown');
      this.providerChartData.datasets[0].data = this.sponsorship.providerships.map((p) => this.getProviderProgress(p));
    }

    // Prepare supplier chart data
    if (this.sponsorship.supplierTransactions && this.sponsorship.supplierTransactions.length > 0) {
      this.supplierChartData.labels = this.sponsorship.supplierTransactions.map(
        (tx) => tx.supplierOffer?.title || 'Unknown'
      );
      this.supplierChartData.datasets[0].data = this.sponsorship.supplierTransactions.map((tx) =>
        this.getSupplierProgress(tx)
      );
    }
  }

  getProviderProgress(p?: ProvidershipLightResponse): number {
    return (p?.totalProduct ?? 0) > 0 ? Math.round(((p?.producedProduct ?? 0) / (p?.totalProduct ?? 0)) * 100) : 0;
  }

  getSupplierProgress(tx: SupplierTransactionLightResponse): number {
    const total = tx.supplierOffer?.quantityAvailable ?? 0;
    const sold = tx.quantitySold ?? 0;
    return total > 0 ? Math.round((sold / total) * 100) : 0;
  }

  getProgressBarClass(percentage: number): string {
    if (percentage < 25) return 'bg-danger';
    if (percentage < 50) return 'bg-warning';
    if (percentage < 75) return 'bg-info';
    return 'bg-success';
  }

  getStatusBadgeClass(status: string): string {
    switch (status?.toUpperCase()) {
      case 'PENDING':
        return 'bg-warning text-dark';
      case 'APPROVED':
        return 'bg-success';
      case 'REJECTED':
        return 'bg-danger';
      case 'FINISHED':
        return 'bg-info';
      case 'PRODUCING':
        return 'bg-primary';
      case 'SELLING':
        return 'bg-info';
      case 'PRODUCING_AND_SELLING':
        return 'bg-success';
      case 'DESIGNING':
        return 'bg-secondary';
      case 'COMPLETED':
        return 'bg-dark';
      default:
        return 'bg-secondary';
    }
  }

  setActiveTab(tab: string): void {
    this.activeTab = tab;
    setTimeout(() => {
      if (this.chart) {
        this.chart.update();
      }
    }, 100);
  }

  exportToPdf(): void {
    // This would be implemented with a PDF generation library like jsPDF
    console.log('Exporting to PDF...');
  }

  exportToCsv(): void {
    // This would create a CSV file with sponsorship data
    console.log('Exporting to CSV...');
  }
}
