package etf.ri.rma.newsfeedapp

object TestS3Data {
    fun getTop3V1(): String {
        return """{
            "meta": {
            "found": 1327753,
            "returned": 10,
            "limit": 10,
            "page": 1
        },
            "data": [
            {
                "uuid": "bec5c4b3-7a0d-431d-acd5-5bc30fd3fa3e",
                "title": "Elon Musk criticism of Trump tax bill frustrates some Republicans: 'No place in Congress'",
                "description": "Elon Musk criticized House Republicans' bill for increasing the budget deficit, while GOP lawmakers argue it benefits middle-class families with tax relief.",
                "keywords": "",
                "snippet": "Elon Muskâ€™s criticism of House Republicansâ€™ \"big, beautiful bill\" has left some GOP lawmakers frustrated at the tech billionaire.\n\n\"This is why Mr. Musk has...",
                "url": "https://www.foxnews.com/politics/elon-musk-criticism-trump-tax-bill-frustrates-some-republicans-no-place-congress",
                "image_url": "https://static.foxnews.com/foxnews.com/content/uploads/2025/02/elon-musk-3.png",
                "language": "en",
                "published_at": "2025-05-29T14:18:53.000000Z",
                "source": "foxnews.com",
                "categories": [
                "politics"
                ],
                "relevance_score": null,
                "locale": "us"
            },
            {
                "uuid": "67bed2c1-716c-430e-8e9e-56e4d8804058",
                "title": "Italian Princess Crashes â€œHeadfirst Into a Wallâ€ While on Motorcycle During Scary Accident",
                "description": "Italian Princess Maria Carolina shared she was involved in a serious motorcycle accident that left her in the ICU, expressing that she was â€œso lucky to be ali...",
                "keywords": "",
                "snippet": "Watch : Why Jeremy Renner Thinks He Died After Horrific Snowplow Accident\n\nPrincess Maria Carolina is recovering after a harrowing accident.\n\nThe Italian royal ...",
                "url": "https://www.eonline.com/news/1418094/princess-maria-carolina-details-motorcycle-accident?cmpid=rss-syndicate-genericrss-us-top_stories",
                "image_url": "https://akns-images.eonline.com/eol_images/Entire_Site/20250529/538a9b13-556c-49dc-abcb-fbec3fda2687_1748527585.jpg?fit=around|1080:1080&output-quality=90&crop=1080:1080;center,top",
                "language": "en",
                "published_at": "2025-05-29T14:18:06.000000Z",
                "source": "eonline.com",
                "categories": [
                "politics"
                ],
                "relevance_score": null,
                "locale": "us"
            },
            {
                "uuid": "236bea50-1dea-4ba7-a481-790af87c3584",
                "title": "Blinkâ€™s budget buzzer gets some worthwhile upgrades",
                "description": "The second-gen Blink video doorbell adds a head-to-toe view, boosts image quality, and brings person detection to the $70 security camera.",
                "keywords": "",
                "snippet": "Amazonâ€™s budget security camera company, Blink, has launched the second generation of its popular video doorbell. The new Blink Video Doorbell adds a head-to-...",
                "url": "https://www.theverge.com/news/676241/blink-launches-second-gen-video-doorbell-with-person-detection",
                "image_url": "https://platform.theverge.com/wp-content/uploads/sites/2/2025/05/Blink-Video-Doorbell-1.jpg?quality=90&strip=all&crop=0,23.821989528796,100,52.356020942408&w=1200",
                "language": "en",
                "published_at": "2025-05-29T14:12:24.000000Z",
                "source": "theverge.com",
                "categories": [
                "politics"
                ],
                "relevance_score": null,
                "locale": "us"
            }
            ]
        }"""
    }

    fun getSimilar(): String {
        return """{
   "meta": {
      "found": 3571,
      "returned": 2,
      "limit": 2,
      "page": 1
   },
   "data": [
      {
         "uuid": "df4ad427-a672-4c67-b6c6-6f81aa00e164",
         "title": "Tesla stock jumps after announcement it will join S&P 500 in one go",
         "description": "Tesla's stock price surged early Tuesday after the company b...",
         "keywords": "Business, s&p 500, stocks, tesla",
         "snippet": "Teslaâ€™s stock price surged early Tuesday after the company...",
         "url": "https://nypost.com/2020/12/01/tesla-stock-jumps-on-news-it-will-join-sp-500-in-one-shot/",
         "image_url": "https://nypost.com/wp-content/uploads/sites/2/2020/12/tesla-52.jpg?quality=90&strip=all&w=1200",
         "language": "en",
         "published_at": "2020-12-01T14:35:46.000000Z",
         "source": "nypost.com",
         "categories": [
            "business"
         ],
         "relevance_score": 153.61266
      },
      {
         "uuid": "c9a23881-12dd-4005-8982-7b6552a2eb50",
         "title": "Tesla To Join S&P 500 With Full Market Cap On December 21",
         "description": "Tesla will be added to the S&P 500 index all at once at its ...",
         "keywords": "Tesla, S&P500, EV, Automotive, Stocks, Investing",
         "snippet": "Tesla (NASDAQ: TSLA) will be added to the S&P 500 index all ...",
         "url": "https://oilprice.com/Latest-Energy-News/World-News/Tesla-To-Join-SP-500-With-Full-Market-Cap-On-December-21.html",
         "image_url": "https://d32r1sh890xpii.cloudfront.net/news/718x300/2020-12-01_xwjdajwctl.jpg",
         "language": "en",
         "published_at": "2020-12-01T16:30:00.000000Z",
         "source": "oilprice.com",
         "categories": [
            "general",
            "business"
         ],
         "relevance_score": 146.92773
      }
   ]
}"""
    }

    fun getTagsV1(): String {
        return """{
  "result": {
    "tags": [
      {
        "confidence": 61.4116096496582,
        "tag": {
          "en": "mountain"
        }
      },
      {
        "confidence": 54.3507270812988,
        "tag": {
          "en": "landscape"
        }
      },
      {
        "confidence": 50.969783782959,
        "tag": {
          "en": "mountains"
        }
      },
      {
        "confidence": 46.1385192871094,
        "tag": {
          "en": "wall"
        }
      },
      {
        "confidence": 40.6059913635254,
        "tag": {
          "en": "clouds"
        }
      },
      {
        "confidence": 37.2282066345215,
        "tag": {
          "en": "sky"
        }
      },
      {
        "confidence": 36.2647514343262,
        "tag": {
          "en": "park"
        }
      },
      {
        "confidence": 35.3734092712402,
        "tag": {
          "en": "national"
        }
      },
      {
        "confidence": 35.1284828186035,
        "tag": {
          "en": "range"
        }
      },
      {
        "confidence": 34.7774543762207,
        "tag": {
          "en": "snow"
        }
      },
      {
        "confidence": 32.9128646850586,
        "tag": {
          "en": "tree"
        }
      },
      {
        "confidence": 29.5557823181152,
        "tag": {
          "en": "rock"
        }
      },
      {
        "confidence": 28.4864749908447,
        "tag": {
          "en": "trees"
        }
      },
      {
        "confidence": 28.1904907226562,
        "tag": {
          "en": "travel"
        }
      },
      {
        "confidence": 28.1532077789307,
        "tag": {
          "en": "valley"
        }
      },
      {
        "confidence": 27.2323837280273,
        "tag": {
          "en": "scenic"
        }
      },
      {
        "confidence": 25.4718055725098,
        "tag": {
          "en": "forest"
        }
      },
      {
        "confidence": 24.6589889526367,
        "tag": {
          "en": "outdoors"
        }
      },
      {
        "confidence": 23.2137584686279,
        "tag": {
          "en": "peak"
        }
      },
      {
        "confidence": 23.1196212768555,
        "tag": {
          "en": "tourism"
        }
      },
      {
        "confidence": 22.9586181640625,
        "tag": {
          "en": "outdoor"
        }
      },
      {
        "confidence": 22.5058460235596,
        "tag": {
          "en": "canyon"
        }
      },
      {
        "confidence": 21.1684589385986,
        "tag": {
          "en": "stone"
        }
      },
      {
        "confidence": 20.7627124786377,
        "tag": {
          "en": "scenery"
        }
      },
      {
        "confidence": 19.8215427398682,
        "tag": {
          "en": "cloud"
        }
      },
      {
        "confidence": 19.6833038330078,
        "tag": {
          "en": "river"
        }
      },
      {
        "confidence": 19.4671821594238,
        "tag": {
          "en": "desert"
        }
      },
      {
        "confidence": 18.9360198974609,
        "tag": {
          "en": "environment"
        }
      },
      {
        "confidence": 16.9691829681396,
        "tag": {
          "en": "rocks"
        }
      },
      {
        "confidence": 16.6996059417725,
        "tag": {
          "en": "lake"
        }
      },
      {
        "confidence": 16.6136302947998,
        "tag": {
          "en": "cliff"
        }
      },
      {
        "confidence": 16.5426540374756,
        "tag": {
          "en": "geology"
        }
      },
      {
        "confidence": 15.9809865951538,
        "tag": {
          "en": "wilderness"
        }
      },
      {
        "confidence": 15.4057178497314,
        "tag": {
          "en": "hiking"
        }
      },
      {
        "confidence": 14.7685861587524,
        "tag": {
          "en": "erosion"
        }
      },
      {
        "confidence": 14.6678800582886,
        "tag": {
          "en": "glacier"
        }
      },
      {
        "confidence": 14.482006072998,
        "tag": {
          "en": "winter"
        }
      },
      {
        "confidence": 14.3086681365967,
        "tag": {
          "en": "panorama"
        }
      },
      {
        "confidence": 14.1589803695679,
        "tag": {
          "en": "summer"
        }
      },
      {
        "confidence": 14.0245943069458,
        "tag": {
          "en": "water"
        }
      },
      {
        "confidence": 13.453519821167,
        "tag": {
          "en": "grass"
        }
      },
      {
        "confidence": 13.1261720657349,
        "tag": {
          "en": "hill"
        }
      },
      {
        "confidence": 13.011589050293,
        "tag": {
          "en": "high"
        }
      },
      {
        "confidence": 12.622181892395,
        "tag": {
          "en": "grand"
        }
      },
      {
        "confidence": 12.6174287796021,
        "tag": {
          "en": "hills"
        }
      },
      {
        "confidence": 12.5902862548828,
        "tag": {
          "en": "rocky"
        }
      },
      {
        "confidence": 12.0642681121826,
        "tag": {
          "en": "sunny"
        }
      },
      {
        "confidence": 11.7458524703979,
        "tag": {
          "en": "landmark"
        }
      },
      {
        "confidence": 11.4653568267822,
        "tag": {
          "en": "vacation"
        }
      },
      {
        "confidence": 11.321738243103,
        "tag": {
          "en": "alp"
        }
      },
      {
        "confidence": 10.7740707397461,
        "tag": {
          "en": "southwest"
        }
      },
      {
        "confidence": 10.5922183990479,
        "tag": {
          "en": "sand"
        }
      },
      {
        "confidence": 10.3427696228027,
        "tag": {
          "en": "cold"
        }
      },
      {
        "confidence": 9.98015022277832,
        "tag": {
          "en": "orange"
        }
      },
      {
        "confidence": 9.7638635635376,
        "tag": {
          "en": "sandstone"
        }
      },
      {
        "confidence": 9.75960826873779,
        "tag": {
          "en": "formation"
        }
      },
      {
        "confidence": 9.669753074646,
        "tag": {
          "en": "ice"
        }
      },
      {
        "confidence": 9.37593650817871,
        "tag": {
          "en": "natural"
        }
      },
      {
        "confidence": 9.03097343444824,
        "tag": {
          "en": "roof"
        }
      },
      {
        "confidence": 8.87552165985107,
        "tag": {
          "en": "peaks"
        }
      },
      {
        "confidence": 8.81966876983643,
        "tag": {
          "en": "alpine"
        }
      },
      {
        "confidence": 8.80224514007568,
        "tag": {
          "en": "mount"
        }
      },
      {
        "confidence": 8.73800754547119,
        "tag": {
          "en": "vista"
        }
      },
      {
        "confidence": 8.6391773223877,
        "tag": {
          "en": "day"
        }
      },
      {
        "confidence": 8.31719589233398,
        "tag": {
          "en": "top"
        }
      },
      {
        "confidence": 8.24748420715332,
        "tag": {
          "en": "peaceful"
        }
      },
      {
        "confidence": 8.17128562927246,
        "tag": {
          "en": "sun"
        }
      },
      {
        "confidence": 8.11302661895752,
        "tag": {
          "en": "horizon"
        }
      },
      {
        "confidence": 7.91500616073608,
        "tag": {
          "en": "land"
        }
      },
      {
        "confidence": 7.91032791137695,
        "tag": {
          "en": "country"
        }
      },
      {
        "confidence": 7.87008666992188,
        "tag": {
          "en": "geological"
        }
      },
      {
        "confidence": 7.86280584335327,
        "tag": {
          "en": "national park"
        }
      },
      {
        "confidence": 7.85683012008667,
        "tag": {
          "en": "spring"
        }
      },
      {
        "confidence": 7.84731531143188,
        "tag": {
          "en": "wild"
        }
      },
      {
        "confidence": 7.79706764221191,
        "tag": {
          "en": "scene"
        }
      },
      {
        "confidence": 7.7928295135498,
        "tag": {
          "en": "color"
        }
      },
      {
        "confidence": 7.72600078582764,
        "tag": {
          "en": "west"
        }
      },
      {
        "confidence": 7.72066307067871,
        "tag": {
          "en": "majestic"
        }
      },
      {
        "confidence": 7.59368371963501,
        "tag": {
          "en": "adventure"
        }
      },
      {
        "confidence": 7.58410120010376,
        "tag": {
          "en": "stones"
        }
      },
      {
        "confidence": 7.51064872741699,
        "tag": {
          "en": "cloudy"
        }
      },
      {
        "confidence": 7.48021507263184,
        "tag": {
          "en": "tourist"
        }
      },
      {
        "confidence": 7.34951877593994,
        "tag": {
          "en": "dome"
        }
      },
      {
        "confidence": 7.33834314346313,
        "tag": {
          "en": "ecology"
        }
      },
      {
        "confidence": 7.24884223937988,
        "tag": {
          "en": "tranquil"
        }
      },
      {
        "confidence": 7.12695741653442,
        "tag": {
          "en": "sunlight"
        }
      }
    ]
  },
  "status": {
    "text": "",
    "type": "success"
  }
}"""
    }
}