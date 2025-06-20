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
