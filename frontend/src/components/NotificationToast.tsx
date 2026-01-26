import React from 'react';

type Props = {
  message: string;
  onClose?: () => void;
  variant?: 'info' | 'error';
  position?: 'top-right' | 'top-left' | 'bottom-right' | 'bottom-left';
};

export default function NotificationToast({ message, onClose, variant = 'info', position = 'top-right' }: Props) {
  const bg = variant === 'error' ? 'bg-red-600' : 'bg-blue-600';
  const posClass =
    position === 'top-right'
      ? 'top-4 right-4'
      : position === 'top-left'
      ? 'top-4 left-4'
      : position === 'bottom-right'
      ? 'bottom-4 right-4'
      : 'bottom-4 left-4';

  return (
    <div className={`fixed ${posClass} z-50 ${bg} text-white px-4 py-2 rounded shadow flex items-center gap-3`} role="status">
      <div className="text-sm truncate max-w-xs">{message}</div>
      {onClose && (
        <button onClick={onClose} className="text-white/90 hover:text-white p-1 rounded">
          <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      )}
    </div>
  );
}
