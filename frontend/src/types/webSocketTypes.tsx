// 웹소켓 관련 타입 정의

// leaderboard 응답 타입
export interface LeaderboardEntry {
    loginId: string;
    psnrAvg: number;
    ssimAvg: number;
    task: string;
    rank: number;
    days : string;
}

export interface LeaderboardResponse {
    status: 'success' | 'fail';
    data: LeaderboardEntry[];
    message: null | string;
}

// WebSocket 메시지 타입
// 공통 베이스 타입
interface WebSocketBaseData {
    token: string;
    userId: string;
    requestId: string;
    loginId: string;
    task: string;
}

export interface MyUserEntry {
    loginId: string;
    psnrAvg: number;
    ssimAvg: number;
    task: string;
    rank: number;
    days: string;
  }
  
  export interface MyUserApiResponse {
    status: 'success' | 'fail';
    data: MyUserEntry[];
    message: string | null;
  }
  

/**
 * Grading 관련 메시지
 */

// 진행중(progress)
export interface GradingProgressData extends WebSocketBaseData {
    current: number;
    total: number;
    filename: string;
}

// 채점 결과
export interface GradingResultData extends WebSocketBaseData {
    psnrAvg: number | null;
    ssimAvg: number | null;
}

/**
 * Task 완료/실패 메시지
 */
export interface TaskResultData extends WebSocketBaseData {
    status: 'success' | 'fail';
}

export type WebSocketMessage =
    | {
        status: 'progress';
        data: GradingProgressData;
        message: string | null;
        }
    | {
        status: 'success' | 'fail';
        data: GradingResultData | TaskResultData;
        message: string | null;
        };

