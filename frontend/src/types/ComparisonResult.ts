export interface ComparisonResult {
    requestId: string;
    userId: string;
    loginId: string;
    psnrAvg: number;
    ssimAvg: number;
    task: string;
    days: string;
}

export interface ComparisonProgress {
    current: number;
    total: number;
    filename: string;
    task: string;
}

export interface WebSocketMessage {
    status: 'progress' | 'success' | 'fail';
    data: ComparisonResult | ComparisonProgress;
    message: string | null;
} 