// src/utils/typeGuards.ts
export const isError = (error: unknown): error is Error => {
    return error instanceof Error
  }
  
  export const isErrorWithMessage = (error: unknown): error is { message: string } => {
    return (
      typeof error === 'object' &&
      error !== null &&
      'message' in error &&
      typeof (error as Record<string, unknown>).message === 'string'
    )
  }
  
  export const toErrorWithMessage = (maybeError: unknown): { message: string } => {
    if (isErrorWithMessage(maybeError)) return maybeError
    
    try {
      return { message: JSON.stringify(maybeError) }
    } catch {
      return { message: String(maybeError) }
    }
  }
  
  // Usage in auth store:
  import { isError, toErrorWithMessage } from '@/utils/typeGuards'
  
  } catch (error: unknown) {
    if (isError(error)) {
      throw new Error(error.message)
    } else {
      const errorWithMessage = toErrorWithMessage(error)
      throw new Error(errorWithMessage.message)
    }
  }
  