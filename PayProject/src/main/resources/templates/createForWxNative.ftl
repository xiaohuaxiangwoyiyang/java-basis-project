<!DOCTYPE html>
<html>
<head>
    <title>支付</title>
</head>
<body>
<div id="myQrcode"></div>
<div id="orderId" hidden>${orderId}</div>
<div id="returnUrl" hidden>${returnUrl}</div>
<script src="https://cdn.bootcss.com/jquery/1.5.1/jquery.min.js"></script>
<script src="https://cdn.bootcss.com/jquery.qrcode/1.0/jquery.qrcode.min.js"></script>
<script>
    $('#myQrcode').qrcode({
        "text": "${codeUrl}"
    });

    $(function () {
        // 定时器
        setInterval(function () {
            console.log("开始查询支付状态。。。111");
            $.ajax({
                url: '/queryByOrderId',
                data: {
                    'orderId': $('#orderId').text()
                },
                success: function (result) {
                    console.log('result', result);
                    console.log("platformStatus", result.platformStatus !== null && result.platformStatus === 'SUCCESS');
                    if (result.platformStatus !== null && result.platformStatus === 'SUCCESS') {
                        location.href = $('#returnUrl').text();
                    }
                },
                error: function (result) {
                    // alert(result);
                    console.log('err', result);
                }
            });
        }, 1000);
    })
</script>

</body>
</html>
