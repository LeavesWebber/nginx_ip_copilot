// Sample Nginx configuration template
export const defaultNginxConfig = `server {
    listen 80;
    server_name example.com;

    location / {
        proxy_pass http://localhost:3000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    # IP blocking rules
    location @blocked_ips {
        return 403 "Access Denied";
    }

    # Country blocking rules
    location @blocked_countries {
        return 403 "Access Denied - Country Blocked";
    }
}`

// Generate Nginx config with IP rules
export const generateNginxConfig = (ipRules: any[], geoRules: any[]) => {
  let config = defaultNginxConfig

  // Add IP blocking rules
  if (ipRules.length > 0) {
    const ipBlockRules = ipRules
      .map(rule => {
        if (rule.type === 'single_ip') {
          return `    deny ${rule.ip};`
        } else {
          return `    deny ${rule.ip_range};`
        }
      })
      .join('\n')
    
    config = config.replace('location / {', `location / {\n${ipBlockRules}`)
  }

  // Add country blocking rules
  if (geoRules.length > 0) {
    const geoBlockRules = geoRules
      .map(rule => `    if ($geoip_country_code = ${rule.country_code}) { return 403; }`)
      .join('\n')
    
    config = config.replace('location / {', `location / {\n${geoBlockRules}`)
  }

  return config
}