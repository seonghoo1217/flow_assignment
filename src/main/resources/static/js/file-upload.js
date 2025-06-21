document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('upload-form');

    form.addEventListener('submit', function (e) {
        e.preventDefault();

        const formData = new FormData(form);

        fetch(form.action, {
            method: 'POST',
            body: formData
        })
            .then(response => {
                if (response.status === 403) {
                    return response.text().then(text => {
                        alert('❌ 차단된 확장자입니다.');
                        throw new Error('Blocked extension');
                    });
                }
                if (response.status === 413) {
                    return response.text().then(text => {
                        alert('❌ 파일 크기가 500KB를 초과했습니다. 500KB 이하만 첨부해주세요.');
                        throw new Error('File too large');
                    })
                }
                if (response.status === 500) {
                    return response.text().then(text => {
                        alert("❌ 서버 오류가 발생했습니다. 나중에 다시 시도해주세요. \n ex.) File Upload 최대치는 500KB입니다.");
                        throw new Error("Server error: " + text);
                    });

                }

                if (!response.ok) {
                    throw new Error('업로드에 실패했습니다. 상태 코드: ' + response.status);
                }
                return response.text();
            })
            .then(msg => {
                alert('✅ 업로드 성공!');
            })
            .catch(err => {
                console.error(err);
            });
    });
});
