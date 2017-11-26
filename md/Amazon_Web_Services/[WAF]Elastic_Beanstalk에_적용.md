# Elastic Beanstalk에 WAF 적용

WAF를 적용하기 위해서는 CloudFront나 ALB를 사용해야 하는데 Elastic Beanstalk의 콘솔에서는 ALB 설정을 지원하지 않는다. ALB를 사용하는 Beanstalk를 사용하기 위해서는 AWS CLI를 통해 Beanstalk 환경을 생성해야 한다.

