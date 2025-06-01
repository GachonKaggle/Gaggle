// API 요청 시 시간이 걸릴 때...
const LoadingSpinner = () => {
    return (
        <div
        style={{
            height: '100vh',
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
        }}
        >
            <div
                style={{
                width: '3rem',
                height: '3rem',
                borderRadius: '9999px',
                borderWidth: '6px',
                borderStyle: 'solid',
                borderColor: '#b2dab1',
                borderTopColor: 'transparent',
                animation: 'spin 1s linear infinite',
                boxSizing: 'border-box',
                }}
                role="status"
            >
                <span
                style={{
                    position: 'absolute',
                    width: '1px',
                    height: '1px',
                    padding: 0,
                    margin: '-1px',
                    overflow: 'hidden',
                    clip: 'rect(0, 0, 0, 0)',
                    whiteSpace: 'nowrap',
                    border: 0,
                }}
                >
                로딩 중...
                </span>
            </div>
        </div>
    );
};

export default LoadingSpinner;
