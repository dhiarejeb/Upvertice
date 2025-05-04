import { Component } from '@angular/core';
import {SupplierTransactionResponse} from '../../../../../services/models/supplier-transaction-response';
import {ActivatedRoute, Router} from '@angular/router';
import {
  SupplierTransactionControllerService
} from '../../../../../services/services/supplier-transaction-controller.service';
import {catchError, of} from 'rxjs';
import {Chart, ChartConfiguration} from 'chart.js';

@Component({
  selector: 'app-supplier-transaction-details',
  standalone: false,
  templateUrl: './supplier-transaction-details.component.html',
  styleUrl: './supplier-transaction-details.component.scss'
})
export class SupplierTransactionDetailsSupplierComponent {
  transaction: SupplierTransactionResponse | null = null
  loading = true
  error = false
  activeTab = "overview"
  salesChart: Chart | null = null
  locationChart: Chart | null = null

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private transactionService: SupplierTransactionControllerService,
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      const id = +params["id"]
      if (id) {
        this.loadTransactionDetails(id)
      } else {
        this.error = true
        this.loading = false
      }
    })
  }

  loadTransactionDetails(id: number): void {
    this.loading = true
    this.error = false

    this.transactionService
      .getTransactionById({ id: id })
      .pipe(
        catchError((err) => {
          console.error("Error loading transaction details", err)
          this.error = true
          this.loading = false
          return of(null)
        }),
      )
      .subscribe((data) => {
        this.transaction = data
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
    if (!this.transaction) return

    // Sales chart
    const salesCtx = document.getElementById("salesChart") as HTMLCanvasElement
    if (salesCtx) {
      if (this.salesChart) {
        this.salesChart.destroy()
      }

      this.salesChart = new Chart(salesCtx, {
        type: "doughnut",
        data: {
          labels: ["Sold", "Available"],
          datasets: [
            {
              data: [
                this.transaction.quantitySold || 0,
                (this.transaction.supplierOffer?.quantityAvailable || 0) - (this.transaction.quantitySold || 0),
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
              text: "Sales Progress",
            },
          },
        },
      } as ChartConfiguration)
    }

    // Location distribution chart
    const locationCtx = document.getElementById("locationChart") as HTMLCanvasElement
    if (locationCtx && this.transaction.locations?.length) {
      if (this.locationChart) {
        this.locationChart.destroy()
      }

      // Count occurrences of each location
      const locationCounts: { [key: string]: number } = {}
      this.transaction.locations.forEach((location) => {
        locationCounts[location] = (locationCounts[location] || 0) + 1
      })

      this.locationChart = new Chart(locationCtx, {
        type: "pie",
        data: {
          labels: Object.keys(locationCounts),
          datasets: [
            {
              data: Object.values(locationCounts),
              backgroundColor: ["#007bff", "#28a745", "#fd7e14", "#6f42c1", "#e83e8c", "#17a2b8", "#ffc107", "#dc3545"],
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
              text: "Location Distribution",
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
      case "FINISHED":
        return "bg-info"
      case "COMPLETED":
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
    this.router.navigate(["/supplier/supplierTransactionManagerSupplier"])
  }

  calculateSalesPercentage(): number {
    if (
      !this.transaction ||
      !this.transaction.supplierOffer?.quantityAvailable ||
      this.transaction.supplierOffer.quantityAvailable === 0
    ) {
      return 0
    }
    return ((this.transaction.quantitySold || 0) / this.transaction.supplierOffer.quantityAvailable) * 100
  }

  formatDate(date: string | undefined): string {
    if (!date) return "N/A"
    return new Date(date).toLocaleDateString()
  }

}
