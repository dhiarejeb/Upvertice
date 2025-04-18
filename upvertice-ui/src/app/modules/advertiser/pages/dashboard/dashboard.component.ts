/*
import { Component, type OnInit, type OnDestroy } from "@angular/core"
import { SponsorshipControllerService } from "../../../../services/services/sponsorship-controller.service"
import { SponsorshipResponse } from "../../../../services/models/sponsorship-response"
import { MatDialog } from "@angular/material/dialog"
import { SnackbarService } from "../../../shared/snackbar/snackbar.service"
import { ConfirmationDialogComponent } from "../../../shared/confirmation-dialog/confirmation-dialog.component"
import { ChartConfiguration, ChartData, ChartType } from "chart.js"
import { Subject } from "rxjs"
import { takeUntil } from "rxjs/operators"

import { ImageViewerComponent } from "../../../shared/image-viewer/image-viewer.component"

interface StatusCount {
  status: string
  count: number
  color: string
  icon: string
}

@Component({
  selector: "app-dashboard",
  standalone : false,
  templateUrl: "./dashboard.component.html",
  styleUrls: ["./dashboard.component.scss"],
})
export class DashboardComponent implements OnInit, OnDestroy {
  // Sponsorships data
  sponsorships: SponsorshipResponse[] = []
  filteredSponsorships: SponsorshipResponse[] = []
  loading = true
  error = false

  // Status filters
  statusFilters: Array<"ALL" | "PENDING" | "APPROVED" | "REJECTED" | "FINISHED"> = [
    "ALL",
    "PENDING",
    "APPROVED",
    "REJECTED",
    "FINISHED",
  ]
  selectedStatus: "ALL" | "PENDING" | "APPROVED" | "REJECTED" | "FINISHED" = "ALL"

  // Pagination
  currentPage = 0
  pageSize = 10
  totalElements = 0
  totalPages = 0

  // Status counts for cards
  statusCounts: StatusCount[] = []



  // Charts
  public doughnutChartData: ChartData<"doughnut"> = {
    labels: [],
    datasets: [
      {
        data: [],
        backgroundColor: [],
      },
    ],
  }

  public doughnutChartType: ChartType = "doughnut"
  public doughnutChartOptions: ChartConfiguration["options"] = {
    responsive: true,
    plugins: {
      legend: {
        display: true,
        position: "right",
      },
      title: {
        display: true,
        text: "Sponsorship Status Distribution",
      },
    },
  }

  public barChartData: ChartData<"bar"> = {
    labels: [],
    datasets: [
      {
        data: [],
        label: "Revenue ($)",
        backgroundColor: "rgba(153, 102, 51, 0.7)",
      },
    ],
  }

  public barChartType: ChartType = "bar"
  public barChartOptions: ChartConfiguration["options"] = {
    responsive: true,
    scales: {
      x: {
        ticks: {
          maxRotation: 45,
          minRotation: 45,
        },
      },
    },
    plugins: {
      legend: {
        display: true,
      },
      title: {
        display: true,
        text: "Revenue by Campaign",
      },
    },
  }

  // Metrics
  totalRevenue = 0
  totalProducts = 0
  totalProviders = 0
  totalTransactions = 0

  // Search
  searchTerm = ""

  // Destroy subject for unsubscribing
  private destroy$ = new Subject<void>()

  constructor(
    private sponsorshipService: SponsorshipControllerService,
    private dialog: MatDialog,
    private snackbar: SnackbarService,
  ) {}

  ngOnInit(): void {
    this.loadSponsorships()
  }

  ngOnDestroy(): void {
    this.destroy$.next()
    this.destroy$.complete()
  }

  loadSponsorships(): void {
    this.loading = true
    this.error = false

    if (this.selectedStatus === "ALL") {
      this.sponsorshipService
        .getAllSponsorships({
          page: this.currentPage,
          size: this.pageSize,
        })
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (response) => {
            this.handleSponsorshipsResponse(response)
          },
          error: (err) => {
            this.handleError(err)
          },
        })
    } else {
      // Cast the selectedStatus to the correct type
      const status = this.selectedStatus as "PENDING" | "APPROVED" | "REJECTED" | "FINISHED"
      this.sponsorshipService
        .getSponsorshipsByStatus({
          status,
          page: this.currentPage,
          size: this.pageSize,
        })
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (response) => {
            this.handleSponsorshipsResponse(response)
          },
          error: (err) => {
            this.handleError(err)
          },
        })
    }
  }

  handleSponsorshipsResponse(response: any): void {
    this.sponsorships = response.content || []
    this.filteredSponsorships = [...this.sponsorships]
    this.totalElements = response.totalElements || 0
    this.totalPages = response.totalPages || 0
    this.calculateMetrics()
    this.calculateStatusCounts()
    this.updateCharts()
    this.loading = false
  }

  handleError(err: any): void {
    console.error("Error loading sponsorships:", err)
    this.snackbar.error("Failed to load sponsorships. Please try again.")
    this.loading = false
    this.error = true
  }

  calculateMetrics(): void {
    this.totalRevenue = this.sponsorships.reduce((sum, s) => sum + (s.sponsorOffer?.price || 0), 0)
    this.totalProducts = this.sponsorships.reduce((sum, s) => sum + (s.sponsorOffer?.productQuantity || 0), 0)
    this.totalProviders = this.sponsorships.reduce((sum, s) => sum + (s.providerships?.length || 0), 0)
    this.totalTransactions = this.sponsorships.reduce((sum, s) => sum + (s.supplierTransactions?.length || 0), 0)
  }

  calculateStatusCounts(): void {
    const counts = {
      PENDING: 0,
      APPROVED: 0,
      REJECTED: 0,
      FINISHED: 0,
    }

    this.sponsorships.forEach((s) => {
      if (s.status && counts.hasOwnProperty(s.status)) {
        counts[s.status as keyof typeof counts]++
      }
    })

    this.statusCounts = [
      {
        status: "PENDING",
        count: counts.PENDING,
        color: "warning",
        icon: "fa-clock",
      },
      {
        status: "APPROVED",
        count: counts.APPROVED,
        color: "success",
        icon: "fa-check-circle",
      },
      {
        status: "REJECTED",
        count: counts.REJECTED,
        color: "danger",
        icon: "fa-times-circle",
      },
      {
        status: "FINISHED",
        count: counts.FINISHED,
        color: "info",
        icon: "fa-flag-checkered",
      },
    ]
  }

  updateCharts(): void {
    // Update doughnut chart
    this.doughnutChartData = {
      labels: this.statusCounts.map((s) => s.status),
      datasets: [
        {
          data: this.statusCounts.map((s) => s.count),
          backgroundColor: [
            "rgba(255, 193, 7, 0.8)", // warning - yellow
            "rgba(40, 167, 69, 0.8)", // success - green
            "rgba(220, 53, 69, 0.8)", // danger - red
            "rgba(23, 162, 184, 0.8)", // info - blue
          ],
        },
      ],
    }

    // Update bar chart
    const topSponsorships = [...this.sponsorships]
      .sort((a, b) => (b.sponsorOffer?.price || 0) - (a.sponsorOffer?.price || 0))
      .slice(0, 5)

    this.barChartData = {
      labels: topSponsorships.map((s) => s.sponsorOffer?.title || "Untitled"),
      datasets: [
        {
          data: topSponsorships.map((s) => s.sponsorOffer?.price || 0),
          label: "Revenue ($)",
          backgroundColor: "rgba(153, 102, 51, 0.7)",
        },
      ],
    }
  }

  onStatusFilterChange(status: "ALL" | "PENDING" | "APPROVED" | "REJECTED" | "FINISHED"): void {
    this.selectedStatus = status
    this.currentPage = 0
    this.loadSponsorships()
  }

  onPageChange(page: number): void {
    this.currentPage = page
    this.loadSponsorships()
  }

  onSearch(): void {
    if (!this.searchTerm.trim()) {
      this.filteredSponsorships = [...this.sponsorships]
      return
    }

    const term = this.searchTerm.toLowerCase().trim()
    this.filteredSponsorships = this.sponsorships.filter(
      (s) =>
        (s.sponsorOffer?.title || "").toLowerCase().includes(term) ||
        (s.sponsorOffer?.description || "").toLowerCase().includes(term) ||
        (s.sponsorOffer?.category || "").toLowerCase().includes(term),
    )
  }

  clearSearch(): void {
    this.searchTerm = ""
    this.filteredSponsorships = [...this.sponsorships]
  }

  calculateProvidershipProgress(sponsorship: SponsorshipResponse): number {
    if (!sponsorship.providerships?.length) return 0
    const completed = sponsorship.providerships.filter((p) => p.status === "COMPLETED").length
    return Math.round((completed / sponsorship.providerships.length) * 100)
  }

  calculateTransactionProgress(sponsorship: SponsorshipResponse): number {
    if (!sponsorship.supplierTransactions?.length) return 0
    const completed = sponsorship.supplierTransactions.filter(
      (t) => t.supplierTransactionStatus === "COMPLETED" || t.supplierTransactionStatus === "FINISHED",
    ).length
    return Math.round((completed / sponsorship.supplierTransactions.length) * 100)
  }

  getStatusClass(status: string | undefined): string {
    if (!status) return "badge-secondary"

    switch (status) {
      case "PENDING":
        return "badge-warning"
      case "APPROVED":
        return "badge-success"
      case "REJECTED":
        return "badge-danger"
      case "FINISHED":
        return "badge-info"
      default:
        return "badge-secondary"
    }
  }

  getStatusIcon(status: string | undefined): string {
    if (!status) return "fa-question-circle"

    switch (status) {
      case "PENDING":
        return "fa-clock"
      case "APPROVED":
        return "fa-check-circle"
      case "REJECTED":
        return "fa-times-circle"
      case "FINISHED":
        return "fa-flag-checkered"
      default:
        return "fa-question-circle"
    }
  }

  formatDate(dateString: string | undefined): string {
    if (!dateString) return "N/A"

    try {
      const date = new Date(dateString)
      return date.toLocaleDateString("en-US", {
        year: "numeric",
        month: "short",
        day: "numeric",
      })
    } catch (e) {
      return "Invalid date"
    }
  }

  /!*viewSponsorshipDetails(sponsorship: SponsorshipResponse): void {
    this.dialog.open(SponsorshipDetailsComponent, {
      width: "800px",
      data: sponsorship,
    })
  }*!/

  viewImage(imageUrl: string): void {
    this.dialog.open(ImageViewerComponent, {
      data: { imageUrl },
      maxWidth: "90vw",
      maxHeight: "90vh",
    })
  }

  async deleteSponsorship(sponsorshipId: number | undefined): Promise<void> {
    if (!sponsorshipId) return

    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      data: {
        title: "Confirm Deletion",
        message: "Are you sure you want to delete this sponsorship? This action cannot be undone.",
      },
    })

    const result = await dialogRef.afterClosed().toPromise()
    if (result) {
      this.sponsorshipService
        .deleteSponsorship({ sponsorshipId })
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            this.snackbar.success("Sponsorship deleted successfully")
            this.loadSponsorships()
          },
          error: (err) => {
            console.error("Error deleting sponsorship:", err)
            this.snackbar.error("Failed to delete sponsorship")
          },
        })
    }
  }

  updateSponsorshipStatus(
    sponsorshipId: number | undefined,
    status: "PENDING" | "APPROVED" | "REJECTED" | "FINISHED",
  ): void {
    if (!sponsorshipId) return

    this.sponsorshipService
      .updateSponsorshipStatus({
        sponsorshipId,
        newStatus: status,
      })
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.snackbar.success(`Sponsorship status updated to ${status}`)
          this.loadSponsorships()
        },
        error: (err) => {
          console.error("Error updating sponsorship status:", err)
          this.snackbar.error("Failed to update sponsorship status")
        },
      })
  }
}
*/
