export class CountryUtils {
  private static exceptions: Map<string, string> = new Map([
    ["AND", "AD"], ["ARE", "AE"], ["ATG", "AG"], ["ARM", "AM"], ["AGO", "AO"],
    ["ATA", "AQ"], ["AUT", "AT"], ["ABW", "AW"], ["ALA", "AX"], ["BIH", "BA"],
    ["BRB", "BB"], ["BGD", "BD"], ["BDI", "BI"], ["BEN", "BJ"], ["BRN", "BN"],
    ["BES", "BQ"], ["BHS", "BS"], ["BLR", "BY"], ["BLZ", "BZ"], ["COD", "CD"],
    ["CAF", "CF"], ["COG", "CG"], ["COK", "CK"], ["CHL", "CL"], ["CHN", "CN"],
    ["CPV", "CV"], ["CUW", "CW"], ["DNK", "DK"], ["EST", "EE"], ["ESH", "EH"],
    ["FLK", "FK"], ["FSM", "FM"], ["FRO", "FO"], ["GRD", "GD"], ["GUF", "GF"],
    ["GRL", "GL"], ["GIN", "GN"], ["GLP", "GP"], ["GNQ", "GQ"], ["SGS", "GS"],
    ["GNB", "GW"], ["GUY", "GY"], ["IRL", "IE"], ["ISR", "IL"], ["IRQ", "IQ"],
    ["JAM", "JM"], ["COM", "KM"], ["PRK", "KP"], ["KOR", "KR"], ["CYM", "KY"],
    ["KAZ", "KZ"], ["LBR", "LR"], ["LBY", "LY"], ["MNE", "ME"], ["MAF", "MF"],
    ["MDG", "MG"], ["MAC", "MO"], ["MNP", "MP"], ["MTQ", "MQ"], ["MLT", "MT"],
    ["MDV", "MV"], ["MEX", "MX"], ["MOZ", "MZ"], ["NIU", "NU"], ["PYF", "PF"],
    ["PNG", "PG"], ["PAK", "PK"], ["POL", "PL"], ["SPM", "PM"], ["PCN", "PN"],
    ["PRT", "PT"], ["PLW", "PW"], ["PRY", "PY"], ["SRB", "RS"], ["SLB", "SB"],
    ["SYC", "SC"], ["SWE", "SE"], ["SVN", "SI"], ["SVK", "SK"], ["SEN", "SN"],
    ["SUR", "SR"], ["SLV", "SV"], ["SWZ", "SZ"], ["TCD", "TD"], ["ATF", "TF"],
    ["TKM", "TM"], ["TUN", "TN"], ["TUR", "TR"], ["TUV", "TV"], ["UKR", "UA"],
    ["URY", "UY"], ["WLF", "WF"], ["MYT", "YT"],
    // non standard iso codes from splash
    ['GER', 'de']
  ]);

  public static countryCodeIso2(countryCodeIso3: string): string {
    const ret = CountryUtils.exceptions.get(countryCodeIso3);
    return ret ? ret : countryCodeIso3.substring(0, 2).toLowerCase();
  }
}
