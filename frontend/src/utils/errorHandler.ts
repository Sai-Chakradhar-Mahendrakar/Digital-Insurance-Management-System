export const handleUnknownError = (error: unknown): string => {
  if (error instanceof Error) {
    return error.message
  } else if (typeof error === 'string') {
    return error
  } else if (error && typeof error === 'object' && 'message' in error) {
    return String(error.message)
  } else {
    return 'An unknown error occurred'
  }
}

