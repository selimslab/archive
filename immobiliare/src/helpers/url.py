class URLManager:
    base_domain = "sahibinden.com"
    base_url = "https://www.sahibinden.com"

    @classmethod
    def check_url(cls, url):
        if cls.base_domain not in url:
            url = "https://" + url + "." + cls.base_domain
        elif "http" not in url:
            url = "https://" + url
        elif "https" not in url:
            url.replace("http", "https")
        return url

    @staticmethod
    def get_subdomain(url):
        return url.replace("https://", "").split(".")[0]
