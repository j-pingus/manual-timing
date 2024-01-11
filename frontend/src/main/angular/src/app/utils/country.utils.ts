export class CountryUtils {
  private static exceptions: Map<string, string> = new Map([
    ["AND", "ad"], ["ARE", "ae"], ["ATG", "ag"], ["ARM", "am"], ["AGO", "ao"],
    ["ATA", "aq"], ["AUT", "at"], ["ABW", "aw"], ["ALA", "ax"], ["BIH", "ba"],
    ["BRB", "bb"], ["BGD", "bd"], ["BDI", "bi"], ["BEN", "bj"], ["BRN", "bn"],
    ["BES", "bq"], ["BHS", "bs"], ["BLR", "by"], ["BLZ", "bz"], ["COD", "cd"],
    ["CAF", "cf"], ["COG", "cg"], ["COK", "ck"], ["CHL", "cl"], ["CHN", "cn"],
    ["CPV", "cv"], ["CUW", "cw"], ["DNK", "dk"], ["EST", "ee"], ["ESH", "eh"],
    ["FLK", "fk"], ["FSM", "fm"], ["FRO", "fo"], ["GRD", "gd"], ["GUF", "gf"],
    ["GRL", "gl"], ["GIN", "gn"], ["GLP", "gp"], ["GNQ", "gq"], ["SGS", "gs"],
    ["GNB", "gw"], ["GUY", "gy"], ["IRL", "ie"], ["ISR", "il"], ["IRQ", "iq"],
    ["JAM", "jm"], ["COM", "km"], ["PRK", "kp"], ["KOR", "kr"], ["CYM", "ky"],
    ["KAZ", "kz"], ["LBR", "lr"], ["LBY", "ly"], ["MNE", "me"], ["MAF", "mf"],
    ["MDG", "mg"], ["MAC", "mo"], ["MNP", "mp"], ["MTQ", "mq"], ["MLT", "mt"],
    ["MDV", "mv"], ["MEX", "mx"], ["MOZ", "mz"], ["NIU", "nu"], ["PYF", "pf"],
    ["PNG", "pg"], ["PAK", "pk"], ["POL", "pl"], ["SPM", "pm"], ["PCN", "pn"],
    ["PRT", "pt"], ["PLW", "pw"], ["PRY", "py"], ["SRB", "rs"], ["SLB", "sb"],
    ["SYC", "sc"], ["SWE", "se"], ["SVN", "si"], ["SVK", "sk"], ["SEN", "sn"],
    ["SUR", "sr"], ["SLV", "sv"], ["SWZ", "sz"], ["TCD", "td"], ["ATF", "tf"],
    ["TKM", "tm"], ["TUN", "tn"], ["TUR", "tr"], ["TUV", "tv"], ["UKR", "ua"],
    ["URY", "uy"], ["WLF", "wf"], ["MYT", "yt"],
    // non standard iso codes from splash
    ['GER', 'de'],
    ['POR', 'pt'],
    ['RSA', 'za']
  ]);

  public static countryCodeIso2(countryCodeIso3: string): string {
    const ret = CountryUtils.exceptions.get(countryCodeIso3);
    return ret ? ret : countryCodeIso3.substring(0, 2).toLowerCase();
  }
}
