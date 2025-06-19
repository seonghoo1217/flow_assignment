document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.extension-checkbox input').forEach(checkbox => {
        checkbox.addEventListener('change', function () {
            const extensionName = this.dataset.name;
            toggleExtension(extensionName);
        });
    });

    document.getElementById('add-extension-btn').addEventListener('click', addCustomExtension);
    document.getElementById('custom-extension-input').addEventListener('keypress', function (e) {
        if (e.key === 'Enter') {
            e.preventDefault();
            addCustomExtension();
        }
    });

    document.querySelectorAll('.delete-btn').forEach(btn => {
        btn.addEventListener('click', function () {
            deleteExtension(this.dataset.name);
        });
    });

    function toggleExtension(extensionName) {
        fetch(`/api/block-extensions/${extensionName}/toggle`, {
            method: 'PATCH',
            headers: {'Content-Type': 'application/json'}
        })
            .then(r => r.ok ? r.json() : Promise.reject())
            .catch(() => showError('확장자 상태 변경에 실패했습니다.'));
    }

    function addCustomExtension() {
        const input = document.getElementById('custom-extension-input');
        const name = input.value.trim().toLowerCase();

        if (!name) return showError('확장자를 입력해주세요.');
        if (!/^[a-zA-Z0-9]+$/.test(name)) return showError('확장자는 영문자와 숫자만 허용됩니다.');
        if (name.length > 20) return showError('확장자는 최대 20자까지 입력 가능합니다.');

        fetch('/api/block-extensions/register', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({extensionName: name})
        })
            .then(r => {
                if (!r.ok) {
                    if (r.status === 400) return r.json().then(data => Promise.reject(data.message));
                    return Promise.reject('확장자 추가에 실패했습니다.');
                }
                return r.json();
            })
            .then(() => window.location.reload())
            .catch(msg => showError(msg));
        input.value = '';
    }

    function deleteExtension(extensionName) {
        fetch(`/api/block-extensions/${extensionName}`, {method: 'DELETE'})
            .then(r => r.ok ? window.location.reload() : Promise.reject())
            .catch(() => showError('확장자 삭제에 실패했습니다.'));
    }

    function showError(msg) {
        const el = document.getElementById('error-message');
        el.textContent = msg;
        el.style.display = 'block';
        setTimeout(() => el.style.display = 'none', 3000);
    }
});
