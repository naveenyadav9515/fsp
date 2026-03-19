import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Task, TaskRequestDto, Page } from '../models/task.model';

/**
 * ============================================================
 * TASK SERVICE
 * ============================================================
 * Handles communication with the backend Task API.
 * The Authorization header is now automatically added by 
 * the AuthInterceptor.
 */
@Injectable({
  providedIn: 'root'
})
export class TaskService {

  private apiUrl = 'http://localhost:8080/api/tasks';

  constructor(private http: HttpClient) { }

  getTasks(page: number = 0, size: number = 10, sortBy: string = 'createdAt', direction: string = 'desc'): Observable<Page<Task>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('direction', direction);

    return this.http.get<Page<Task>>(this.apiUrl, { params });
  }

  getTask(id: string): Observable<Task> {
    return this.http.get<Task>(`${this.apiUrl}/${id}`);
  }

  createTask(task: TaskRequestDto): Observable<Task> {
    return this.http.post<Task>(this.apiUrl, task);
  }

  updateTask(id: string, task: TaskRequestDto): Observable<Task> {
    return this.http.put<Task>(`${this.apiUrl}/${id}`, task);
  }

  updateTaskStatus(id: string, status: string): Observable<Task> {
    let params = new HttpParams().set('status', status);
    return this.http.patch<Task>(`${this.apiUrl}/${id}/status`, {}, { params });
  }

  deleteTask(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
