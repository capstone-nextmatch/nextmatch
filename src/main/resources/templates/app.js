// Spring Boot 백엔드 서버의 기본 URL (CORS 설정을 위해 지정한 포트와 다를 수 있음)
const API_BASE_URL = 'http://localhost:8080/api/wishlist';
const WISHLIST_CONTAINER = document.getElementById('wishlist-container');
const MEMBER_ID_INPUT = document.getElementById('memberIdInput');

/**
 * 1. 관심 대회 목록을 백엔드에서 조회합니다. (GET 요청)
 */
async function loadWishlist() {
    const memberId = MEMBER_ID_INPUT.value;
    if (!memberId) return;

    // 로딩 상태 표시
    WISHLIST_CONTAINER.innerHTML = '<li>데이터를 불러오는 중...</li>';

    try {
        const response = await fetch(`${API_BASE_URL}/${memberId}`);

        if (!response.ok) {
            throw new Error(`HTTP 오류: ${response.status}`);
        }

        const data = await response.json();

        // 목록 렌더링
        renderWishlist(data);

    } catch (error) {
        console.error('관심 목록 조회 실패:', error);
        WISHLIST_CONTAINER.innerHTML = `<li>오류 발생: ${error.message}. 백엔드 서버를 확인하세요.</li>`;
    }
}

/**
 * 2. 조회된 데이터를 HTML 목록으로 화면에 그립니다.
 */
function renderWishlist(items) {
    if (items.length === 0) {
        WISHLIST_CONTAINER.innerHTML = '<li>등록된 관심 대회가 없습니다.</li>';
        return;
    }

    // 기존 내용을 지웁니다.
    WISHLIST_CONTAINER.innerHTML = '';

    items.forEach(item => {
        const listItem = document.createElement('li');
        const formattedDate = new Date(item.registeredAt).toLocaleDateString();

        listItem.innerHTML = `
            <strong>${item.contestTitle}</strong> (대회 ID: ${item.contestId})
            <span class="date">등록일: ${formattedDate}</span>
            <button class="delete-btn" data-wish-id="${item.wishId}">관심 해제</button>
        `;

        // '관심 해제' 버튼에 이벤트 리스너 추가
        listItem.querySelector('.delete-btn').addEventListener('click', (event) => {
            const wishId = event.target.dataset.wishId;
            removeWishItem(wishId);
        });

        WISHLIST_CONTAINER.appendChild(listItem);
    });
}

/**
 * 3. 관심 대회 항목을 삭제합니다. (DELETE 요청)
 */
async function removeWishItem(wishId) {
    if (!confirm('정말로 이 대회를 관심 목록에서 삭제하시겠습니까?')) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/${wishId}`, {
            method: 'DELETE'
        });

        if (response.status === 204) {
            alert('관심 대회에서 성공적으로 해제되었습니다.');
            // 삭제 후 목록을 다시 불러와 화면을 업데이트합니다.
            loadWishlist();
        } else {
            // Spring Boot에서 정의한 예외 처리 응답을 여기서 파싱해야 합니다.
            // 여기서는 단순하게 상태 코드로 오류 처리
            throw new Error(`삭제 실패. HTTP Status: ${response.status}`);
        }

    } catch (error) {
        console.error('관심 대회 삭제 실패:', error);
        alert(`삭제 중 오류 발생: ${error.message}`);
    }
}


/**
 * 4. [테스트] 관심 대회 항목을 추가합니다. (POST 요청)
 */
async function addWishItem() {
    const memberId = MEMBER_ID_INPUT.value;
    const contestId = document.getElementById('contestIdInput').value;

    if (!memberId || !contestId) {
        alert("회원 ID와 대회 ID를 모두 입력해주세요.");
        return;
    }

    try {
        const response = await fetch(API_BASE_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                memberId: Number(memberId),
                contestId: Number(contestId)
            })
        });

        if (response.status === 201) { // 201 Created
            alert('관심 대회로 등록되었습니다!');
            loadWishlist(); // 목록 새로고침
        } else {
            const errorData = await response.json(); // 백엔드 예외 메시지 확인
            alert(`등록 실패: ${errorData.message || '서버 오류'}`);
        }
    } catch (error) {
        console.error('등록 실패:', error);
        alert('등록 중 네트워크 오류가 발생했습니다.');
    }
}


// 페이지 로딩 시 초기 목록 조회 함수를 호출합니다.
window.onload = loadWishlist;