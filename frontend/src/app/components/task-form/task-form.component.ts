import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TaskService } from '../../services/task.service';
import { TaskRequestDto } from '../../models/task.model';

@Component({
  selector: 'app-task-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './task-form.component.html',
  styleUrl: './task-form.component.scss'
})
export class TaskFormComponent {
  @Output() taskCreated = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();

  // Default priority to MEDIUM
  task: TaskRequestDto = { title: '', description: '', priority: 'MEDIUM' };
  isSubmitting = false;
  error: string | null = null;

  constructor(private taskService: TaskService) {}

  onSubmit(): void {
    if (!this.task.title.trim()) {
      this.error = 'Task title is required.';
      return;
    }
    
    this.isSubmitting = true;
    this.error = null;

    this.taskService.createTask(this.task).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.taskCreated.emit();
      },
      error: (err) => {
        this.isSubmitting = false;
        
        if (err.status === 400 && err.error?.details) {
            const details = Object.values(err.error.details).join(', ');
            this.error = `Validation Error: ${details}`;
        } else {
            this.error = 'Failed to create task. Check if the server is running.';
        }
        console.error('Creation failed', err);
      }
    });
  }
}
