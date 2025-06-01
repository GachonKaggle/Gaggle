import React, { createContext, useContext, useState } from 'react';
import { ComparisonResult } from '../types/ComparisonResult';

interface ResultContextType {
    result: ComparisonResult | null;
    setResult: (result: ComparisonResult | null) => void;
    isLoading: boolean;
    setIsLoading: (loading: boolean) => void;
    progress: { current: number; total: number } | null;
    setProgress: (progress: { current: number; total: number } | null) => void;
}

const ResultContext = createContext<ResultContextType | undefined>(undefined);

export const ResultProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const [result, setResult] = useState<ComparisonResult | null>(null);
    const [isLoading, setIsLoading] = useState(false);
    const [progress, setProgress] = useState<{ current: number; total: number } | null>(null);

    return (
        <ResultContext.Provider value={{
            result,
            setResult,
            isLoading,
            setIsLoading,
            progress,
            setProgress
        }}>
            {children}
        </ResultContext.Provider>
    );
};

export const useResult = () => {
    const context = useContext(ResultContext);
    if (context === undefined) {
        throw new Error('useResult must be used within a ResultProvider');
    }
    return context;
}; 