import { Component } from '@angular/core';
import {ProvidershipResponse} from '../../../../../services/models/providership-response';
import {ActivatedRoute, Router} from '@angular/router';
import {ProvidershipControllerService} from '../../../../../services/services/providership-controller.service';
import {catchError, of} from 'rxjs';
import {Chart, ChartConfiguration} from 'chart.js';

@Component({
  selector: 'app-providership-details',
  standalone: false,
  templateUrl: './providership-details.component.html',
  styleUrl: './providership-details.component.scss'
})
export class ProvidershipDetailsProviderComponent {
  providership: ProvidershipResponse | null = null
  loading = true
  error = false
  activeTab = "overview"
  productionChart: Chart | null = null
  transactionsChart: Chart | null = null

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private providershipService: ProvidershipControllerService,
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      const id = +params["id"]
      if (id) {
        this.loadProvidershipDetails(id)
      } else {
        this.error = true
        this.loading = false
      }
    })
  }

  loadProvidershipDetails(id: number): void {
    this.loading = true
    this.error = false

    this.providershipService
      .getProvidershipById({ providershipId: id })
      .pipe(
        catchError((err) => {
          console.error("Error loading providership details", err)
          this.error = true
          this.loading = false
          return of(null)
        }),
      )
      .subscribe((data) => {
        this.providership = data
        this.loading = false
        if (data) {
          setTimeout(() => {
            this.renderCharts()
          }, 100)
        }
      })
  }

  setActiveTab(tab: string): void {
    this.activeTab = tab
    // Re-render charts when switching to tabs that contain them
    if (tab === "overview" || tab === "analytics") {
      setTimeout(() => {
        this.renderCharts()
      }, 100)
    }
  }

  renderCharts(): void {
    if (!this.providership) return

    // Production progress chart
    const productionCtx = document.getElementById("productionChart") as HTMLCanvasElement
    if (productionCtx) {
      if (this.productionChart) {
        this.productionChart.destroy()
      }

      this.productionChart = new Chart(productionCtx, {
        type: "doughnut",
        data: {
          labels: ["Produced", "Remaining"],
          datasets: [
            {
              data: [
                this.providership.producedProduct || 0,
                (this.providership.totalProduct || 0) - (this.providership.producedProduct || 0),
              ],
              backgroundColor: ["#28a745", "#e9ecef"],
            },
          ],
        },
        options: {
          responsive: true,
          plugins: {
            legend: {
              position: "bottom",
            },
            title: {
              display: true,
              text: "Production Progress",
            },
          },
        },
      } as ChartConfiguration)
    }

    // Transactions chart
    const transactionsCtx = document.getElementById("transactionsChart") as HTMLCanvasElement
    if (transactionsCtx && this.providership.sponsorship?.supplierTransactions?.length) {
      if (this.transactionsChart) {
        this.transactionsChart.destroy()
      }

      const transactions = this.providership.sponsorship.supplierTransactions

      this.transactionsChart = new Chart(transactionsCtx, {
        type: "bar",
        data: {
          labels: transactions.map((t) => t.supplierOffer?.title || "Unknown"),
          datasets: [
            {
              label: "Quantity Sold",
              data: transactions.map((t) => t.quantitySold || 0),
              backgroundColor: "#007bff",
            },
            {
              label: "Relative Price",
              data: transactions.map((t) => t.relativePrice || 0),
              backgroundColor: "#fd7e14",
            },
          ],
        },
        options: {
          responsive: true,
          scales: {
            y: {
              beginAtZero: true,
            },
          },
          plugins: {
            legend: {
              position: "bottom",
            },
            title: {
              display: true,
              text: "Supplier Transactions",
            },
          },
        },
      } as ChartConfiguration)
    }
  }

  getStatusClass(status: string | undefined): string {
    if (!status) return "bg-secondary"

    switch (status) {
      case "PENDING":
        return "bg-warning"
      case "APPROVED":
        return "bg-success"
      case "REJECTED":
        return "bg-danger"
      case "DESIGNING":
        return "bg-info"
      case "PRODUCING":
        return "bg-primary"
      case "PRODUCING_AND_SELLING":
        return "bg-primary"
      case "SELLING":
        return "bg-info"
      case "COMPLETED":
        return "bg-success"
      case "FINISHED":
        return "bg-success"
      case "COMING_SOON":
        return "bg-warning"
      case "AVAILABLE":
        return "bg-success"
      case "CLOSED":
        return "bg-secondary"
      default:
        return "bg-secondary"
    }
  }

  goBack(): void {
    this.router.navigate(["/provider/providerships"])
  }

  calculateCompletionPercentage(): number {
    if (!this.providership || !this.providership.totalProduct || this.providership.totalProduct === 0) {
      return 0
    }
    return ((this.providership.producedProduct || 0) / this.providership.totalProduct) * 100
  }


  // at the bottom of ProvidershipDetailsComponent
  /** true if any supplier-transaction has at least one proof image */
  get hasTransactionProofs(): boolean {
    return !!this.providership
      ?.sponsorship
      ?.supplierTransactions
      ?.some(tx => Array.isArray(tx.proofs) && tx.proofs.length > 0);
  }

}
