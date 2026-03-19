import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TaskService } from '../../services/task.service';
import { Task, Page } from '../../models/task.model';
import { TaskFormComponent } from '../task-form/task-form.component';

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [CommonModule, FormsModule, TaskFormComponent],
  templateUrl: './task-list.component.html',
  styleUrl: './task-list.component.scss'
})
export class TaskListComponent implements OnInit {
  tasks: Task[] = [];
  currentPage = 0;
  totalPages = 0;
  totalElements = 0;

  isLoading = true;
  error: string | null = null;
  showForm = false;

  constructor(private taskService: TaskService) {}

  ngOnInit(): void {
    this.loadTasks();
  }

  loadTasks(page: number = 0): void {
    this.isLoading = true;
    this.error = null;
    this.taskService.getTasks(page).subscribe({
      next: (data: Page<Task>) => {
        this.tasks = data.content;
        this.currentPage = data.number;
        this.totalPages = data.totalPages;
        this.totalElements = data.totalElements;
        this.isLoading = false;
      },
      error: (err) => {
        if (err.status === 403 || err.status === 401) {
          this.error = 'Session expired or not authorized. Please log in again.';
        } else {
          this.error = 'Failed to load tasks. Check if backend is running.';
        }
        this.isLoading = false;
        console.error('API Error:', err);
      }
    });
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.loadTasks(this.currentPage + 1);
    }
  }

  prevPage(): void {
    if (this.currentPage > 0) {
      this.loadTasks(this.currentPage - 1);
    }
  }

  updateStatus(id: string, newStatus: string): void {
    if (!id) return;
    this.taskService.updateTaskStatus(id, newStatus).subscribe({
      next: () => this.loadTasks(this.currentPage),
      error: (err) => console.error('Failed to update status', err)
    });
  }

  deleteTask(id: string): void {
    if (!id) return;
    if (confirm('Are you sure you want to delete this task?')) {
      this.taskService.deleteTask(id).subscribe({
        next: () => {
          // If we delete the last item on the page, go back a page
          const targetPage = (this.tasks.length === 1 && this.currentPage > 0) ? this.currentPage - 1 : this.currentPage;
          this.loadTasks(targetPage);
        },
        error: (err) => console.error('Failed to delete task', err)
      });
    }
  }

  toggleForm(): void {
    this.showForm = !this.showForm;
  }

  onTaskCreated(): void {
    this.showForm = false;
    this.loadTasks(0); // Go back to first page to see the new task (assuming desc sort)
  }

  getPriorityClass(priority: string): string {
    return 'priority-' + priority?.toLowerCase();
  }

  getStatusClass(status: string): string {
    return 'status-' + status?.toLowerCase();
  }
}
