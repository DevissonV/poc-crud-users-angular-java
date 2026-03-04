/**
 * Metadata de respuestas de la API
 */
export interface ApiMetadata {
  timestamp: string;
  status: number;
  message: string;
  totalItems?: number;
}

/**
 * Estructura genérica de respuesta de la API
 */
export interface ApiResponse<T> {
  data: T;
  metadata: ApiMetadata;
}

/**
 * Datos de error en respuestas de error
 */
export interface ErrorData {
  error: string;
  path: string;
  validationErrors?: { [key: string]: string };
}
